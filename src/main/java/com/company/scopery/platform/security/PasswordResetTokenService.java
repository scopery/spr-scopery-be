package com.company.scopery.platform.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Stores only SHA-256 hashes of password-reset tokens in Redis.
 * Raw tokens are returned once to the caller for email delivery and never persisted.
 */
@Service
public class PasswordResetTokenService {

    private static final String PREFIX = "scopery:password_reset:";
    private static final long TTL_MINUTES = 30;

    private final StringRedisTemplate redisTemplate;

    public PasswordResetTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String create(UUID userId) {
        String rawToken = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(keyFor(rawToken), userId.toString(), TTL_MINUTES, TimeUnit.MINUTES);
        return rawToken;
    }

    public Optional<UUID> consume(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }
        String key = keyFor(rawToken);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        redisTemplate.delete(key);
        return Optional.of(UUID.fromString(value));
    }

    private static String keyFor(String rawToken) {
        return PREFIX + sha256(rawToken);
    }

    static String sha256(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
