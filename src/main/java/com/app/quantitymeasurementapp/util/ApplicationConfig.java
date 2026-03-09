package com.app.quantitymeasurementapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final String PROPERTIES_FILE = "application.properties";
    private static ApplicationConfig instance;

    private final Properties properties = new Properties();

    private ApplicationConfig() {
        loadProperties();
    }

    public static synchronized ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (is == null) {
                logger.warn("application.properties not found on classpath, using defaults");
                return;
            }
            properties.load(is);
            logger.info("Loaded configuration from {}", PROPERTIES_FILE);
        } catch (IOException e) {
            logger.error("Failed to load {}: {}", PROPERTIES_FILE, e.getMessage());
        }
    }

    public String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        return properties.getProperty(key, "");
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getDbUrl()       { return get("db.url",      "jdbc:h2:mem:quantitydb;DB_CLOSE_DELAY=-1"); }
    public String getDbUsername()  { return get("db.username", "sa"); }
    public String getDbPassword()  { return get("db.password", ""); }
    public String getDbDriver()    { return get("db.driver",   "org.h2.Driver"); }

    public int    getPoolInitialSize()       { return getInt("pool.initialSize",     5); }
    public int    getPoolMaxSize()           { return getInt("pool.maxSize",         10); }
    public long   getPoolConnectionTimeout() { return getLong("pool.connectionTimeout", 30000L); }

    public String getRepositoryType() { return get("repository.type", "database"); }

    @Override
    public String toString() {
        return String.format("ApplicationConfig{dbUrl=%s, poolMax=%d, repoType=%s}",
            getDbUrl(), getPoolMaxSize(), getRepositoryType());
    }
}
