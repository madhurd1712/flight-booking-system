package com.flight.booking.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RedisRateLimiterConfig {

    /**
     * Rate limiting based on IP address
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String ipAddress = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ipAddress);
        };
    }

    /**
     * Alternative: Rate limiting based on username from JWT
     * Uncomment to use this instead
     */
    /*
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String username = exchange.getRequest().getHeaders().getFirst("X-User-Name");
            return Mono.just(username != null ? username : "anonymous");
        };
    }
    */
}
