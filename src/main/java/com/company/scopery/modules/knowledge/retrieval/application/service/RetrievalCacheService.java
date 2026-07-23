package com.company.scopery.modules.knowledge.retrieval.application.service;

import com.company.scopery.modules.aiagent.tool.application.port.AiToolResultItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Short-lived Redis cache for retrieval results.
 * Key: retrieval:v1:{projectId}:{revision}:{aclHash}:{queryHash}
 * TTL: 5 minutes. Revision segment auto-invalidates on knowledge content change.
 */
@Service
public class RetrievalCacheService {

    private static final Logger log = LoggerFactory.getLogger(RetrievalCacheService.class);
    private static final String KEY_PREFIX = "retrieval:v1:";
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final TypeReference<List<AiToolResultItem>> ITEMS_TYPE = new TypeReference<>() {};

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RetrievalCacheService(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<List<AiToolResultItem>> get(UUID projectId, String revision,
                                                 List<String> aclTokens, String query) {
        String key = buildKey(projectId, revision, aclTokens, query);
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, ITEMS_TYPE));
        } catch (Exception e) {
            log.warn("[RetrievalCache] get failed key={}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    public void put(UUID projectId, String revision,
                    List<String> aclTokens, String query, List<AiToolResultItem> items) {
        String key = buildKey(projectId, revision, aclTokens, query);
        try {
            String json = objectMapper.writeValueAsString(items);
            stringRedisTemplate.opsForValue().set(key, json, TTL);
        } catch (Exception e) {
            log.warn("[RetrievalCache] put failed key={}: {}", key, e.getMessage());
        }
    }

    private String buildKey(UUID projectId, String revision, List<String> aclTokens, String query) {
        String aclHash = sha256(aclTokens == null ? "" :
                aclTokens.stream().sorted().collect(Collectors.joining(",")));
        String queryHash = sha256(query != null ? query : "");
        return KEY_PREFIX + projectId + ":" + revision + ":" + aclHash + ":" + queryHash;
    }

    private static String sha256(String text) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
