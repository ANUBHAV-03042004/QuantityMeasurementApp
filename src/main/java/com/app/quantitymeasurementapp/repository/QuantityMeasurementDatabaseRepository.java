package com.app.quantitymeasurementapp.repository;

import com.app.quantitymeasurementapp.entity.QuantityDTO;
import com.app.quantitymeasurementapp.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurementapp.exception.DatabaseException;
import com.app.quantitymeasurementapp.util.ApplicationConfig;
import com.app.quantitymeasurementapp.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementDatabaseRepository.class);

    private static final String INSERT_ENTITY =
        "INSERT INTO quantity_measurement_entity "
        + "(id, operation_type, operand1_value, operand1_unit, operand1_type, "
        + "operand2_value, operand2_unit, operand2_type, "
        + "result_value, result_unit, result_type, "
        + "comparison_result, has_error, error_message, created_at) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_HISTORY =
        "INSERT INTO quantity_measurement_history "
        + "(entity_id, operation_type, measurement_type, summary, recorded_at) "
        + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL =
        "SELECT * FROM quantity_measurement_entity ORDER BY created_at DESC";

    private static final String SELECT_BY_OPERATION =
        "SELECT * FROM quantity_measurement_entity WHERE operation_type = ? ORDER BY created_at DESC";

    private static final String SELECT_BY_TYPE =
        "SELECT * FROM quantity_measurement_entity WHERE operand1_type = ? ORDER BY created_at DESC";

    private static final String SELECT_COUNT =
        "SELECT COUNT(*) FROM quantity_measurement_entity";

    private static final String DELETE_ALL_HISTORY =
        "DELETE FROM quantity_measurement_history";

    private static final String DELETE_ALL_ENTITIES =
        "DELETE FROM quantity_measurement_entity";

    private final ConnectionPool connectionPool;

    public QuantityMeasurementDatabaseRepository(ApplicationConfig config) {
        logger.info("Initializing QuantityMeasurementDatabaseRepository");
        this.connectionPool = new ConnectionPool(config);
        initializeSchema();
        logger.info("Database repository ready. {}", connectionPool.getPoolStatistics());
    }

    public QuantityMeasurementDatabaseRepository(ConnectionPool connectionPool) {
        logger.info("Initializing QuantityMeasurementDatabaseRepository with provided pool");
        this.connectionPool = connectionPool;
        initializeSchema();
    }

    private void initializeSchema() {
        String schemaFile = "/db/schema-test.sql";
        try (InputStream is = getClass().getResourceAsStream(schemaFile)) {
            if (is == null) {
                logger.warn("Schema file not found: {}, attempting without schema init", schemaFile);
                return;
            }
            String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
            Connection conn = connectionPool.acquireConnection();
            try {
                conn.setAutoCommit(false);
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(trimmed);
                        }
                    }
                }
                conn.commit();
                logger.info("Database schema initialized successfully");
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Schema initialization failed", "INIT", e);
            } finally {
                conn.setAutoCommit(true);
                connectionPool.releaseConnection(conn);
            }
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Could not read schema file", "INIT", e);
        }
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        if (entity == null) throw new DatabaseException("Cannot save null entity", "SAVE");
        Connection conn = connectionPool.acquireConnection();
        try {
            conn.setAutoCommit(false);
            insertEntity(conn, entity);
            insertHistory(conn, entity);
            conn.commit();
            logger.debug("Entity saved to database. id={}", entity.getOperationId());
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DatabaseException("Failed to save entity", "SAVE", e);
        } finally {
            setAutoCommitQuietly(conn);
            connectionPool.releaseConnection(conn);
        }
    }

    private void insertEntity(Connection conn, QuantityMeasurementEntity e) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_ENTITY)) {
            ps.setString(1, e.getOperationId());
            ps.setString(2, e.getOperationType());

            setQuantityParams(ps, 3, e.getOperand1());
            setNullableQuantityParams(ps, 6, e.getOperand2());
            setNullableQuantityParams(ps, 9, e.getResult());

            ps.setBoolean(12, e.getComparisonResult());
            ps.setBoolean(13, e.hasError());
            ps.setString(14, e.getErrorMessage());
            ps.setTimestamp(15, Timestamp.valueOf(e.getTimestamp() != null ? e.getTimestamp() : LocalDateTime.now()));

            ps.executeUpdate();
        }
    }

    private void insertHistory(Connection conn, QuantityMeasurementEntity e) throws SQLException {
        String measurementType = e.getOperand1() != null && e.getOperand1().getUnit() != null
            ? e.getOperand1().getUnit().getMeasurementType() : "UNKNOWN";
        try (PreparedStatement ps = conn.prepareStatement(INSERT_HISTORY)) {
            ps.setString(1, e.getOperationId());
            ps.setString(2, e.getOperationType());
            ps.setString(3, measurementType);
            ps.setString(4, e.toString());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }
    }

    private void setQuantityParams(PreparedStatement ps, int startIndex, QuantityDTO dto) throws SQLException {
        if (dto == null || dto.getUnit() == null) {
            ps.setDouble(startIndex, 0.0);
            ps.setString(startIndex + 1, "UNKNOWN");
            ps.setString(startIndex + 2, "UNKNOWN");
        } else {
            ps.setDouble(startIndex, dto.getValue());
            ps.setString(startIndex + 1, dto.getUnit().getUnitName());
            ps.setString(startIndex + 2, dto.getUnit().getMeasurementType());
        }
    }

    private void setNullableQuantityParams(PreparedStatement ps, int startIndex, QuantityDTO dto) throws SQLException {
        if (dto == null || dto.getUnit() == null) {
            ps.setNull(startIndex, Types.DOUBLE);
            ps.setNull(startIndex + 1, Types.VARCHAR);
            ps.setNull(startIndex + 2, Types.VARCHAR);
        } else {
            ps.setDouble(startIndex, dto.getValue());
            ps.setString(startIndex + 1, dto.getUnit().getUnitName());
            ps.setString(startIndex + 2, dto.getUnit().getMeasurementType());
        }
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return executeQuery(SELECT_ALL, ps -> {});
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType) {
        if (operationType == null) return new ArrayList<>();
        return executeQuery(SELECT_BY_OPERATION, ps -> ps.setString(1, operationType.toUpperCase()));
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        if (measurementType == null) return new ArrayList<>();
        return executeQuery(SELECT_BY_TYPE, ps -> ps.setString(1, measurementType.toUpperCase()));
    }

    @Override
    public int getTotalCount() {
        Connection conn = connectionPool.acquireConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_COUNT);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count measurements", "COUNT", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    @Override
    public void deleteAll() {
        Connection conn = connectionPool.acquireConnection();
        try {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(DELETE_ALL_HISTORY);
                stmt.execute(DELETE_ALL_ENTITIES);
            }
            conn.commit();
            logger.info("All measurements deleted from database");
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DatabaseException("Failed to delete all measurements", "DELETE", e);
        } finally {
            setAutoCommitQuietly(conn);
            connectionPool.releaseConnection(conn);
        }
    }

    @Override
    public void clear() {
        deleteAll();
    }

    @Override
    public String getPoolStatistics() {
        return connectionPool.getPoolStatistics();
    }

    @Override
    public void releaseResources() {
        connectionPool.shutdown();
        logger.info("Database repository resources released");
    }

    private interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    private List<QuantityMeasurementEntity> executeQuery(String sql, PreparedStatementSetter setter) {
        Connection conn = connectionPool.acquireConnection();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + sql, "QUERY", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return results;
    }

    private QuantityMeasurementEntity mapRow(ResultSet rs) throws SQLException {
        String opType        = rs.getString("operation_type");
        boolean hasError     = rs.getBoolean("has_error");
        String errorMessage  = rs.getString("error_message");

        QuantityDTO operand1 = buildDTO(rs.getDouble("operand1_value"), rs.getString("operand1_unit"), rs.getString("operand1_type"));
        QuantityDTO operand2 = buildNullableDTO(rs, "operand2_value", "operand2_unit", "operand2_type");
        QuantityDTO result   = buildNullableDTO(rs, "result_value", "result_unit", "result_type");
        boolean compResult   = rs.getBoolean("comparison_result");

        if (hasError) return new QuantityMeasurementEntity(opType, operand1, operand2, errorMessage);
        if ("COMPARE".equals(opType)) return new QuantityMeasurementEntity(opType, operand1, operand2, compResult);
        if (operand2 == null) return new QuantityMeasurementEntity(opType, operand1, result);
        return new QuantityMeasurementEntity(opType, operand1, operand2, result);
    }

    private QuantityDTO buildDTO(double value, String unitName, String measurementType) {
        if (unitName == null || measurementType == null) return null;
        QuantityDTO.IMeasurableUnit unit = resolveUnit(unitName, measurementType);
        return new QuantityDTO(value, unit);
    }

    private QuantityDTO buildNullableDTO(ResultSet rs, String valueCol, String unitCol, String typeCol) throws SQLException {
        Object rawValue = rs.getObject(valueCol);
        if (rawValue == null) return null;
        return buildDTO(rs.getDouble(valueCol), rs.getString(unitCol), rs.getString(typeCol));
    }

    private QuantityDTO.IMeasurableUnit resolveUnit(String unitName, String measurementType) {
        try {
            switch (measurementType) {
                case "LENGTH":      return QuantityDTO.LengthUnit.valueOf(unitName);
                case "WEIGHT":      return QuantityDTO.WeightUnit.valueOf(unitName);
                case "VOLUME":      return QuantityDTO.VolumeUnit.valueOf(unitName);
                case "TEMPERATURE": return QuantityDTO.TemperatureUnit.valueOf(unitName);
                default: {
                    final String u = unitName;
                    final String t = measurementType;
                    return new QuantityDTO.IMeasurableUnit() {
                        @Override public String getUnitName()        { return u; }
                        @Override public String getMeasurementType() { return t; }
                    };
                }
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown unit {} for type {}", unitName, measurementType);
            final String u = unitName;
            final String t = measurementType;
            return new QuantityDTO.IMeasurableUnit() {
                @Override public String getUnitName()        { return u; }
                @Override public String getMeasurementType() { return t; }
            };
        }
    }

    private void rollbackQuietly(Connection conn) {
        try { if (conn != null) conn.rollback(); }
        catch (SQLException e) { logger.warn("Rollback failed: {}", e.getMessage()); }
    }

    private void setAutoCommitQuietly(Connection conn) {
        try { if (conn != null) conn.setAutoCommit(true); }
        catch (SQLException e) { logger.warn("setAutoCommit failed: {}", e.getMessage()); }
    }
}
