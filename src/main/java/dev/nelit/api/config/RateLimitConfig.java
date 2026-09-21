package dev.nelit.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Configuration
@SuppressWarnings("rawtypes")
public class RateLimitConfig {

    @Bean
    public RedisScript<List> rateLimiterScript() {
        return RedisScript.of(new ClassPathResource("scripts/rate_limiter.lua"), List.class);
    }
}