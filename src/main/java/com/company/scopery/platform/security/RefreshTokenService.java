package com.company.scopery.platform.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private static final String KEY_PREFIX = "scopery:refresh_token:";

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties       jwtProperties;

    public RefreshTokenService(StringRedisTemplate redisTemplate, JwtProperties jwtProperties) {
        this.redisTemplate = redisTemplate;
        this.jwtProperties = jwtProperties;
    }

    public String create(UUID userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(
                KEY_PREFIX + token,
                userId.toString(),
                jwtProperties.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS);
        return token;
    }

    public Optional<UUID> validate(String token) {
        String raw = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        return Optional.ofNullable(raw).map(UUID::fromString);
    }

    public void revoke(String token) {
        if (token != null && !token.isBlank()) {
            redisTemplate.delete(KEY_PREFIX + token);
        }
    }
}
