package com.company.scopery.modules.traceability.aimapping.application.internal;

import com.company.scopery.modules.knowledge.indexing.infrastructure.embedding.EmbeddingProvider;
import com.company.scopery.modules.traceability.aimapping.shared.config.AiMappingProperties;
import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;
import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummary;
import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MappingSummaryBuilderService {

    private static final Logger log = LoggerFactory.getLogger(MappingSummaryBuilderService.class);

    private final MappingSummaryRepository summaryRepository;
    private final EmbeddingProvider embeddingProvider;
    private final AiMappingProperties properties;
    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    public MappingSummaryBuilderService(MappingSummaryRepository summaryRepository,
                                        EmbeddingProvider embeddingProvider,
                                        AiMappingProperties properties,
                                        JdbcTemplate jdbc,
                                        ObjectMapper objectMapper) {
        this.summaryRepository = summaryRepository;
        this.embeddingProvider = embeddingProvider;
        this.properties = properties;
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }

    public MappingSummary getOrBuildSummary(SummaryEntityType entityType, UUID entityId) {
        Optional<MappingSummary> cached = summaryRepository.findByEntityTypeAndEntityId(entityType, entityId);
        EntityData data = loadEntityData(entityType, entityId);

        if (data == null) {
            log.warn("Entity data not found for type={} id={}", entityType, entityId);
            return null;
        }

        String compactText = buildCompactText(entityType, data);
        String hash = sha256(compactText);
        String structuredJson = buildStructuredJson(entityType, data);

        if (cached.isPresent()) {
            MappingSummary existing = cached.get();
            if (existing.entityVersion() == data.version() && existing.summaryHash().equals(hash)) {
                return existing;
            }
            // Version or content changed → rebuild with same ID to update (merge)
            MappingSummary updated = new MappingSummary(
                    existing.id(), entityType, entityId, data.version(),
                    compactText, structuredJson, hash, java.time.Instant.now()
            );
            MappingSummary saved = summaryRepository.save(updated);
            refreshEmbedding(saved.id(), compactText);
            return saved;
        }

        MappingSummary newSummary = MappingSummary.create(entityType, entityId, data.version(),
                compactText, structuredJson, hash);
        MappingSummary saved = summaryRepository.save(newSummary);
        refreshEmbedding(saved.id(), compactText);
        return saved;
    }

    private void refreshEmbedding(UUID summaryId, String compactText) {
        try {
            List<float[]> embeddings = embeddingProvider.embed(List.of(compactText), properties.getEmbeddingModel());
            if (!embeddings.isEmpty()) {
                summaryRepository.updateEmbedding(summaryId, embeddings.get(0));
            }
        } catch (Exception e) {
            log.warn("Failed to generate embedding for summary {}: {}", summaryId, e.getMessage());
        }
    }

    private EntityData loadEntityData(SummaryEntityType entityType, UUID entityId) {
        return switch (entityType) {
            case REQUIREMENT -> loadRequirement(entityId);
            case FUNCTION -> loadFunction(entityId);
            case USE_CASE -> loadUseCase(entityId);
            case TEST_CASE -> loadTestCase(entityId);
        };
    }

    private EntityData loadRequirement(UUID id) {
        String sql = """
                SELECT code, title, description, requirement_type, priority, status,
                       current_version_number, COALESCE(version, 0) AS version
                FROM requirements_requirement WHERE id = ?::uuid
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id.toString());
        if (rows.isEmpty()) return null;
        Map<String, Object> r = rows.get(0);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("entityType", "REQUIREMENT");
        fields.put("code", r.get("code"));
        fields.put("title", r.get("title"));
        fields.put("description", r.get("description"));
        fields.put("requirementType", r.get("requirement_type"));
        fields.put("priority", r.get("priority"));
        fields.put("status", r.get("status"));
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    private EntityData loadFunction(UUID id) {
        String sql = """
                SELECT code, title, description, priority, status, type, version
                FROM app_functional_item WHERE id = ?::uuid
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id.toString());
        if (rows.isEmpty()) return null;
        Map<String, Object> r = rows.get(0);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("entityType", "FUNCTION");
        fields.put("code", r.get("code"));
        fields.put("title", r.get("title"));
        fields.put("description", r.get("description"));
        fields.put("priority", r.get("priority"));
        fields.put("status", r.get("status"));
        fields.put("type", r.get("type"));
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    private EntityData loadUseCase(UUID id) {
        String sql = """
                SELECT key, name, goal, primary_actor_name, trigger_text, status, version
                FROM app_use_case WHERE id = ?::uuid
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id.toString());
        if (rows.isEmpty()) return null;
        Map<String, Object> r = rows.get(0);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("entityType", "USE_CASE");
        fields.put("key", r.get("key"));
        fields.put("name", r.get("name"));
        fields.put("goal", r.get("goal"));
        fields.put("primaryActorName", r.get("primary_actor_name"));
        fields.put("triggerText", r.get("trigger_text"));
        fields.put("status", r.get("status"));
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    private EntityData loadTestCase(UUID id) {
        String sql = """
                SELECT code, title, description, type, priority, status,
                       preconditions, expected_result, version
                FROM quality_test_case WHERE id = ?::uuid
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id.toString());
        if (rows.isEmpty()) return null;
        Map<String, Object> r = rows.get(0);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("entityType", "TEST_CASE");
        fields.put("code", r.get("code"));
        fields.put("title", r.get("title"));
        fields.put("description", r.get("description"));
        fields.put("type", r.get("type"));
        fields.put("priority", r.get("priority"));
        fields.put("status", r.get("status"));
        fields.put("preconditions", r.get("preconditions"));
        fields.put("expectedResult", r.get("expected_result"));
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    private String buildCompactText(SummaryEntityType type, EntityData data) {
        Map<String, Object> f = data.fields();
        return switch (type) {
            case REQUIREMENT -> String.format("[REQ] %s: %s. Type: %s. %s",
                    f.get("code"), f.get("title"), f.get("requirementType"),
                    nvl(f.get("description")));
            case FUNCTION -> String.format("[FUNC] %s: %s. %s",
                    f.get("code"), f.get("title"), nvl(f.get("description")));
            case USE_CASE -> String.format("[UC] %s: %s. Goal: %s. Actor: %s. Trigger: %s",
                    f.get("key"), f.get("name"), nvl(f.get("goal")),
                    nvl(f.get("primaryActorName")), nvl(f.get("triggerText")));
            case TEST_CASE -> String.format("[TC] %s: %s. Type: %s. %s Pre: %s. Expected: %s",
                    f.get("code"), f.get("title"), f.get("type"),
                    nvl(f.get("description")), nvl(f.get("preconditions")), nvl(f.get("expectedResult")));
        };
    }

    private String buildStructuredJson(SummaryEntityType type, EntityData data) {
        try {
            return objectMapper.writeValueAsString(data.fields());
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private static String nvl(Object v) {
        return v != null ? v.toString() : "";
    }

    private static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    record EntityData(int version, Map<String, Object> fields) {}
}
