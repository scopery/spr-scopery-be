package com.company.scopery.modules.knowledge.retrieval.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AclSignatureService {

    private final ObjectMapper objectMapper;

    public AclSignatureService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy()
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    /**
     * Computes ACL signature: "acl:v1:sha256:<64 lowercase hex>"
     * Input is canonical JSON with sorted keys, sorted+deduped aclTokens, UTF-8, no extra whitespace.
     */
    public String computeSignature(UUID workspaceId, UUID projectId,
                                   String classification, List<String> aclTokens,
                                   String sourceAccessVersion) {
        try {
            Map<String, Object> canonical = buildCanonical(workspaceId, projectId,
                    classification, aclTokens, sourceAccessVersion);
            byte[] json = objectMapper.writeValueAsBytes(canonical);
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(json);
            return "acl:v1:sha256:" + toHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        } catch (Exception e) {
            throw new RuntimeException("ACL signature computation failed", e);
        }
    }

    private Map<String, Object> buildCanonical(UUID workspaceId, UUID projectId,
                                               String classification, List<String> aclTokens,
                                               String sourceAccessVersion) {
        List<String> sortedTokens = new ArrayList<>(aclTokens != null ? aclTokens : Collections.emptyList());
        sortedTokens = sortedTokens.stream().distinct().sorted().toList();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("aclTokens", sortedTokens);
        map.put("classification", classification != null ? classification : "");
        map.put("projectId", projectId != null ? projectId.toString() : "");
        map.put("sourceAccessVersion", sourceAccessVersion != null ? sourceAccessVersion : "");
        map.put("workspaceId", workspaceId.toString());
        return map;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
