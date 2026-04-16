package com.microservices.product.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import java.time.Duration;
import java.util.Objects;

@Configuration
@EnableCaching
public class RedisConfig {
@Bean
public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Objects.requireNonNull(Duration.ofMinutes(60))); // Garantie non-nulle
}
}