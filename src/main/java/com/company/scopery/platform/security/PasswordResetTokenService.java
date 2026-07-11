package com.company.scopery.platform.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetTokenService {
    private static final String PREFIX = "scopery:password_reset:";
    private final StringRedisTemplate redisTemplate;
    public PasswordResetTokenService(StringRedisTemplate redisTemplate) { this.redisTemplate = redisTemplate; }
    public String create(UUID userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(PREFIX + token, userId.toString(), 30, TimeUnit.MINUTES);
        return token;
    }
    public Optional<UUID> consume(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        String key = PREFIX + token;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        redisTemplate.delete(key);
        return Optional.of(UUID.fromString(value));
    }
}
