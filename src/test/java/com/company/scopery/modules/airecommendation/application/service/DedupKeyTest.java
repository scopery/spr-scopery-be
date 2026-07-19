package com.company.scopery.modules.airecommendation.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DedupKeyTest {

    private RecommendationDeduplicationService service;

    private final UUID workspaceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID projectId   = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID targetId    = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    void setUp() {
        service = new RecommendationDeduplicationService();
    }

    @Test
    void canonicalJson_stableAcrossMapOrdering() {
        Map<String, Object> payloadAlpha = new TreeMap<>();
        payloadAlpha.put("aKey", "value");
        payloadAlpha.put("zKey", "other");

        Map<String, Object> payloadReverse = new LinkedHashMap<>();
        payloadReverse.put("zKey", "other");
        payloadReverse.put("aKey", "value");

        String json1 = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", targetId, "TASK_MISSING_OWNER", 1, payloadAlpha);
        String json2 = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", targetId, "TASK_MISSING_OWNER", 1, payloadReverse);

        assertThat(json1).isEqualTo(json2);
    }

    @Test
    void dedupKey_deterministic_sameInputSameKey() {
        Map<String, Object> payload = Map.of("field", "value");

        String json = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", targetId, "TASK_MISSING_OWNER", 1, payload);

        String key1 = service.computeDedupKey(json);
        String key2 = service.computeDedupKey(json);

        assertThat(key1).isEqualTo(key2);
    }

    @Test
    void dedupKey_startsWithExpectedPrefix() {
        String json = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", targetId, "TASK_MISSING_OWNER", 1, Map.of());
        String key = service.computeDedupKey(json);
        assertThat(key).startsWith("rec:v1:sha256:");
    }

    @Test
    void dedupKey_has78Characters() {
        // "rec:v1:sha256:" = 14 chars + 64 hex chars = 78 total
        String json = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", targetId, "TASK_MISSING_OWNER", 1, Map.of());
        String key = service.computeDedupKey(json);
        assertThat(key).hasSize(78);
    }

    @Test
    void dedupKey_differentSuggestionType_differentKey() {
        Map<String, Object> payload = Map.of();
        String json1 = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", targetId, "TASK_MISSING_OWNER", 1, payload);
        String json2 = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_ESTIMATE", "TASK", targetId, "TASK_MISSING_ESTIMATE", 1, payload);
        assertThat(service.computeDedupKey(json1)).isNotEqualTo(service.computeDedupKey(json2));
    }

    @Test
    void dedupKey_differentTargetId_differentKey() {
        UUID otherId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Map<String, Object> payload = Map.of();
        String json1 = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", targetId, "TASK_MISSING_OWNER", 1, payload);
        String json2 = service.computeCanonicalJson(workspaceId, projectId,
                "TASK_MISSING_OWNER", "TASK", otherId, "TASK_MISSING_OWNER", 1, payload);
        assertThat(service.computeDedupKey(json1)).isNotEqualTo(service.computeDedupKey(json2));
    }

    @Test
    void suppressionKey_startsWithExpectedPrefix() {
        UUID actorId = UUID.randomUUID();
        String key = service.computeSuppressionKey(workspaceId, projectId, actorId, "TARGET", "scopeKey");
        assertThat(key).startsWith("recsup:v1:sha256:");
    }

    @Test
    void suppressionKey_deterministic() {
        UUID actorId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        String key1 = service.computeSuppressionKey(workspaceId, projectId, actorId, "TARGET", "key");
        String key2 = service.computeSuppressionKey(workspaceId, projectId, actorId, "TARGET", "key");
        assertThat(key1).isEqualTo(key2);
    }

    @Test
    void suppressionKey_differentScopeType_differentKey() {
        UUID actorId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        String keyTarget = service.computeSuppressionKey(workspaceId, projectId, actorId, "TARGET", "key");
        String keyType   = service.computeSuppressionKey(workspaceId, projectId, actorId, "TYPE", "key");
        assertThat(keyTarget).isNotEqualTo(keyType);
    }
}
