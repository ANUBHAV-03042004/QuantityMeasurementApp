package com.app.quantitymeasurementapp.util;

import com.app.quantitymeasurementapp.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private final String          url;
    private final String          username;
    private final String          password;
    private final int             maxSize;
    private final long            timeoutMs;

    private final BlockingQueue<Connection> available;
    private final List<Connection>          allConnections;
    private final AtomicInteger             activeCount = new AtomicInteger(0);
    private volatile boolean                closed      = false;

    public ConnectionPool(ApplicationConfig config) {
        this.url          = config.getDbUrl();
        this.username     = config.getDbUsername();
        this.password     = config.getDbPassword();
        this.maxSize      = config.getPoolMaxSize();
        this.timeoutMs    = config.getPoolConnectionTimeout();

        int initial = config.getPoolInitialSize();
        this.available      = new ArrayBlockingQueue<>(maxSize);
        this.allConnections = new ArrayList<>(maxSize);

        loadDriver(config.getDbDriver());
        initializeConnections(initial);

        logger.info("ConnectionPool initialized with {} connections (max {})", initial, maxSize);
    }

    private void loadDriver(String driverClass) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("JDBC driver not found: " + driverClass, "INIT", e);
        }
    }

    private void initializeConnections(int count) {
        for (int i = 0; i < count; i++) {
            try {
                Connection conn = DriverManager.getConnection(url, username, password);
                available.offer(conn);
                allConnections.add(conn);
            } catch (SQLException e) {
                logger.error("Failed to create connection {}: {}", i, e.getMessage());
                throw new DatabaseException("Could not initialize connection pool", "INIT", e);
            }
        }
    }

    public Connection acquireConnection() {
        if (closed) throw new DatabaseException("Connection pool is closed", "ACQUIRE");

        try {
            Connection conn = available.poll(timeoutMs, TimeUnit.MILLISECONDS);
            if (conn != null) {
                if (!isValid(conn)) {
                    conn = createNewConnection();
                }
                activeCount.incrementAndGet();
                logger.debug("Connection acquired. Active: {}", activeCount.get());
                return conn;
            }

            if (allConnections.size() < maxSize) {
                Connection newConn = createNewConnection();
                activeCount.incrementAndGet();
                allConnections.add(newConn);
                logger.debug("New connection created. Total: {}, Active: {}", allConnections.size(), activeCount.get());
                return newConn;
            }

            throw new DatabaseException("Connection pool exhausted. All " + maxSize + " connections are in use.", "ACQUIRE");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Interrupted while waiting for connection", "ACQUIRE", e);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection == null) return;
        try {
            if (isValid(connection) && !closed) {
                available.offer(connection);
                activeCount.decrementAndGet();
                logger.debug("Connection released. Active: {}", activeCount.get());
            } else {
                closeQuietly(connection);
                activeCount.decrementAndGet();
            }
        } catch (Exception e) {
            logger.warn("Error releasing connection: {}", e.getMessage());
        }
    }

    private Connection createNewConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create new connection", "CREATE", e);
        }
    }

    private boolean isValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private void closeQuietly(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            logger.warn("Error closing connection: {}", e.getMessage());
        }
    }

    public String getPoolStatistics() {
        return String.format("ConnectionPool{total=%d, active=%d, available=%d, max=%d}",
            allConnections.size(), activeCount.get(), available.size(), maxSize);
    }

    public int getActiveCount()    { return activeCount.get(); }
    public int getAvailableCount() { return available.size(); }
    public int getTotalCount()     { return allConnections.size(); }

    public void shutdown() {
        closed = true;
        for (Connection conn : allConnections) {
            closeQuietly(conn);
        }
        allConnections.clear();
        available.clear();
        logger.info("ConnectionPool shut down");
    }
}
