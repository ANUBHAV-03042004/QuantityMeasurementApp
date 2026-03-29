package com.app.quantitymeasurementapp.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Cache configuration.
 *
 *  - dev  profile → {@link ConcurrentMapCacheManager} (in-memory, no Redis needed)
 *  - prod profile → {@link RedisCacheManager} (connects to Redis configured in application-prod.properties)
 *
 * Cache names:
 *   - "historyByOperation"    — results of getHistoryByOperation()
 *   - "historyByType"         — results of getHistoryByMeasurementType()
 *   - "errorHistory"          — results of getErrorHistory()
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * DEV: simple in-memory cache. No Redis server required.
     */
    @Bean
    @Profile("dev")
    public CacheManager devCacheManager() {
        return new ConcurrentMapCacheManager(
                "historyByOperation", "historyByType", "errorHistory");
    }

    /**
     * PROD: Redis-backed cache with 10-minute TTL and JSON serialisation.
     */
    @Bean
    @Profile("prod")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("historyByOperation",
                        config.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("historyByType",
                        config.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("errorHistory",
                        config.entryTtl(Duration.ofMinutes(2)))
                .build();
    }
}
