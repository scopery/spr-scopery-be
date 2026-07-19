package com.company.scopery.modules.airecommendation.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class RecommendationDeduplicationService {

    private static final String DEDUP_PREFIX = "rec:v1:sha256:";
    private static final String SUPPRESSION_PREFIX = "recsup:v1:sha256:";

    private final ObjectMapper sortedMapper;

    public RecommendationDeduplicationService() {
        this.sortedMapper = new ObjectMapper()
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    public String computeCanonicalJson(UUID workspaceId, UUID projectId,
                                       String suggestionType, String targetEntityType, UUID targetEntityId,
                                       String schemaCode, int schemaVersion,
                                       Map<String, Object> normalizedPayload) {
        try {
            TreeMap<String, Object> canonical = new TreeMap<>();
            canonical.put("workspaceId", workspaceId.toString().toLowerCase());
            canonical.put("projectId", projectId.toString().toLowerCase());
            canonical.put("suggestionType", suggestionType);
            canonical.put("targetEntityType", targetEntityType);
            canonical.put("targetEntityId", targetEntityId.toString().toLowerCase());
            canonical.put("schemaCode", schemaCode);
            canonical.put("schemaVersion", schemaVersion);
            canonical.put("payload", normalizeSorted(normalizedPayload));
            return sortedMapper.writeValueAsString(canonical);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to compute canonical JSON for dedup key", e);
        }
    }

    public String computeDedupKey(String canonicalJson) {
        return DEDUP_PREFIX + sha256Hex(canonicalJson.getBytes(StandardCharsets.UTF_8));
    }

    public String computePayloadHash(byte[] canonicalPayloadBytes) {
        return sha256Hex(canonicalPayloadBytes);
    }

    public String computeSuppressionKey(UUID workspaceId, UUID projectId,
                                        UUID actorId, String scopeType, String scopeKey) {
        String raw = workspaceId.toString().toLowerCase()
                + ":" + projectId.toString().toLowerCase()
                + ":" + actorId.toString().toLowerCase()
                + ":" + scopeType
                + ":" + (scopeKey != null ? scopeKey : "");
        return SUPPRESSION_PREFIX + sha256Hex(raw.getBytes(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("unchecked")
    private Object normalizeSorted(Object value) {
        if (value instanceof Map<?, ?> map) {
            TreeMap<String, Object> sorted = new TreeMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                sorted.put(String.valueOf(entry.getKey()), normalizeSorted(entry.getValue()));
            }
            return sorted;
        }
        return value;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
