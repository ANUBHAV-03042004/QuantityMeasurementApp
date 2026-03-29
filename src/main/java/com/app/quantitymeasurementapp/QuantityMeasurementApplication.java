package com.app.quantitymeasurementapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Application entry point.
 *
 * @EnableCaching activates Spring's proxy-based caching infrastructure.
 * The actual CacheManager bean (in-memory or Redis) is chosen based on
 * the active Spring profile (dev → ConcurrentMapCache, prod → Redis).
 */
@SpringBootApplication
@EnableCaching
public class QuantityMeasurementApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementApplication.class, args);
    }
}
