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
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
            case FUNCTION    -> loadFunction(entityId);
            case USE_CASE    -> loadUseCase(entityId);
            case TEST_CASE   -> loadTestCase(entityId);
        };
    }

    // -------------------------------------------------------------------------
    // REQUIREMENT — only title + description (no type/priority/status in compact)
    // -------------------------------------------------------------------------

    private EntityData loadRequirement(UUID id) {
        String sql = """
                SELECT code, title, description, COALESCE(version, 0) AS version
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
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    // -------------------------------------------------------------------------
    // FUNCTION — full context: criteria JSONB, business rules, screens, APIs,
    //            comm specs, existing use cases
    // -------------------------------------------------------------------------

    private EntityData loadFunction(UUID id) {
        String sql = """
                SELECT code, title, description, acceptance_criteria::text, version
                FROM app_functional_item WHERE id = ?::uuid
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id.toString());
        if (rows.isEmpty()) return null;
        Map<String, Object> r = rows.get(0);

        List<String> acceptanceCriteria = parseJsonArray(r.get("acceptance_criteria"));

        List<String> businessRules = jdbc.queryForList(
                "SELECT title FROM app_business_rule WHERE functional_item_id = ?::uuid AND status = 'ACTIVE' ORDER BY code LIMIT 10",
                String.class, id.toString());

        List<String> screens = jdbc.queryForList(
                "SELECT s.name FROM app_function_screen fs JOIN app_registry_screen s ON s.id = fs.screen_id WHERE fs.function_id = ?::uuid",
                String.class, id.toString());

        List<String> apis = jdbc.queryForList(
                "SELECT e.name FROM app_function_api fa JOIN app_registry_api_endpoint e ON e.id = fa.api_endpoint_id WHERE fa.function_id = ?::uuid",
                String.class, id.toString());

        List<String> comms = jdbc.queryForList(
                "SELECT c.name FROM app_function_communication fc JOIN app_communication_specification c ON c.id = fc.communication_id WHERE fc.function_id = ?::uuid",
                String.class, id.toString());

        List<String> useCases = jdbc.queryForList(
                "SELECT name FROM app_use_case WHERE primary_function_id = ?::uuid AND status NOT IN ('ARCHIVED','DEPRECATED') LIMIT 10",
                String.class, id.toString());

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("entityType", "FUNCTION");
        fields.put("code", r.get("code"));
        fields.put("title", r.get("title"));
        fields.put("description", r.get("description"));
        if (!acceptanceCriteria.isEmpty()) fields.put("acceptanceCriteria", acceptanceCriteria);
        if (!businessRules.isEmpty()) fields.put("businessRules", businessRules);
        if (!screens.isEmpty()) fields.put("linkedScreens", screens);
        if (!apis.isEmpty()) fields.put("linkedApis", apis);
        if (!comms.isEmpty()) fields.put("linkedComms", comms);
        if (!useCases.isEmpty()) fields.put("existingUseCases", useCases);
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    // -------------------------------------------------------------------------
    // USE CASE — goal, actor, trigger, preconditions, acceptance criteria, rules
    // -------------------------------------------------------------------------

    private EntityData loadUseCase(UUID id) {
        String sql = """
                SELECT key, name, goal, primary_actor_name, trigger_text, status, version
                FROM app_use_case WHERE id = ?::uuid
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id.toString());
        if (rows.isEmpty()) return null;
        Map<String, Object> r = rows.get(0);

        List<String> preconditions = jdbc.queryForList(
                "SELECT content FROM app_use_case_condition WHERE use_case_id = ?::uuid AND condition_type = 'PRECONDITION' ORDER BY display_order LIMIT 5",
                String.class, id.toString());

        List<Map<String, Object>> criteria = jdbc.queryForList(
                "SELECT title, given_text, when_text, then_text FROM app_use_case_acceptance_criterion WHERE use_case_id = ?::uuid ORDER BY display_order LIMIT 5",
                id.toString());

        List<String> rules = jdbc.queryForList(
                "SELECT description FROM app_use_case_business_rule WHERE use_case_id = ?::uuid ORDER BY display_order LIMIT 5",
                String.class, id.toString());

        List<String> criteriaTexts = criteria.stream()
                .map(c -> buildGherkinLine(c))
                .collect(Collectors.toList());

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("entityType", "USE_CASE");
        fields.put("key", r.get("key"));
        fields.put("name", r.get("name"));
        fields.put("goal", r.get("goal"));
        fields.put("primaryActorName", r.get("primary_actor_name"));
        fields.put("triggerText", r.get("trigger_text"));
        if (!preconditions.isEmpty()) fields.put("preconditions", preconditions);
        if (!criteriaTexts.isEmpty()) fields.put("acceptanceCriteria", criteriaTexts);
        if (!rules.isEmpty()) fields.put("businessRules", rules);
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    // -------------------------------------------------------------------------
    // TEST CASE — title, preconditions, expected result, test steps
    // -------------------------------------------------------------------------

    private EntityData loadTestCase(UUID id) {
        String sql = """
                SELECT code, title, description, type, preconditions, expected_result, version
                FROM quality_test_case WHERE id = ?::uuid
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id.toString());
        if (rows.isEmpty()) return null;
        Map<String, Object> r = rows.get(0);

        List<Map<String, Object>> steps = jdbc.queryForList(
                "SELECT action, expected_result FROM quality_test_case_step WHERE test_case_id = ?::uuid ORDER BY sort_order LIMIT 10",
                id.toString());

        List<String> stepTexts = steps.stream()
                .map(s -> nvl(s.get("action")) + " → " + nvl(s.get("expected_result")))
                .collect(Collectors.toList());

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("entityType", "TEST_CASE");
        fields.put("code", r.get("code"));
        fields.put("title", r.get("title"));
        fields.put("type", r.get("type"));
        fields.put("preconditions", r.get("preconditions"));
        fields.put("expectedResult", r.get("expected_result"));
        if (!stepTexts.isEmpty()) fields.put("testSteps", stepTexts);
        int version = ((Number) r.getOrDefault("version", 0)).intValue();
        return new EntityData(version, fields);
    }

    // -------------------------------------------------------------------------
    // Compact text builders
    // -------------------------------------------------------------------------

    private String buildCompactText(SummaryEntityType type, EntityData data) {
        Map<String, Object> f = data.fields();
        return switch (type) {
            case REQUIREMENT -> String.format("[REQ] %s: %s. %s",
                    f.get("code"), f.get("title"), nvl(f.get("description")));
            case FUNCTION -> buildFunctionCompact(f);
            case USE_CASE -> buildUseCaseCompact(f);
            case TEST_CASE -> buildTestCaseCompact(f);
        };
    }

    private String buildFunctionCompact(Map<String, Object> f) {
        StringBuilder sb = new StringBuilder();
        sb.append("[FUNC] ").append(f.get("code")).append(": ").append(f.get("title")).append(".");
        if (f.get("description") != null) sb.append(" ").append(f.get("description")).append(".");
        appendList(sb, "Criteria", f.get("acceptanceCriteria"));
        appendList(sb, "Rules", f.get("businessRules"));
        appendList(sb, "Screens", f.get("linkedScreens"));
        appendList(sb, "APIs", f.get("linkedApis"));
        appendList(sb, "Comms", f.get("linkedComms"));
        appendList(sb, "UseCases", f.get("existingUseCases"));
        return sb.toString();
    }

    private String buildUseCaseCompact(Map<String, Object> f) {
        StringBuilder sb = new StringBuilder();
        sb.append("[UC] ").append(f.get("key")).append(": ").append(f.get("name")).append(".");
        sb.append(" Goal: ").append(nvl(f.get("goal"))).append(".");
        sb.append(" Actor: ").append(nvl(f.get("primaryActorName"))).append(".");
        sb.append(" Trigger: ").append(nvl(f.get("triggerText"))).append(".");
        appendList(sb, "Pre", f.get("preconditions"));
        appendList(sb, "Criteria", f.get("acceptanceCriteria"));
        appendList(sb, "Rules", f.get("businessRules"));
        return sb.toString();
    }

    private String buildTestCaseCompact(Map<String, Object> f) {
        StringBuilder sb = new StringBuilder();
        sb.append("[TC] ").append(f.get("code")).append(": ").append(f.get("title")).append(".");
        sb.append(" Type: ").append(f.get("type")).append(".");
        sb.append(" Pre: ").append(nvl(f.get("preconditions"))).append(".");
        appendList(sb, "Steps", f.get("testSteps"));
        sb.append(" Expected: ").append(nvl(f.get("expectedResult"))).append(".");
        return sb.toString();
    }

    private void appendList(StringBuilder sb, String label, Object value) {
        if (value instanceof List<?> list && !list.isEmpty()) {
            sb.append(" ").append(label).append(": [")
              .append(list.stream().map(Object::toString).collect(Collectors.joining(", ")))
              .append("].");
        }
    }

    private String buildStructuredJson(SummaryEntityType type, EntityData data) {
        try {
            return objectMapper.writeValueAsString(data.fields());
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String buildGherkinLine(Map<String, Object> c) {
        StringBuilder sb = new StringBuilder();
        if (c.get("title") != null) sb.append(c.get("title")).append(": ");
        if (c.get("given_text") != null) sb.append("Given ").append(c.get("given_text")).append(" ");
        if (c.get("when_text") != null) sb.append("When ").append(c.get("when_text")).append(" ");
        if (c.get("then_text") != null) sb.append("Then ").append(c.get("then_text"));
        return sb.toString().trim();
    }

    private List<String> parseJsonArray(Object jsonValue) {
        if (jsonValue == null) return List.of();
        try {
            Object parsed = objectMapper.readValue(jsonValue.toString(), Object.class);
            if (parsed instanceof List<?> list) {
                List<String> result = new ArrayList<>();
                for (Object item : list) {
                    if (item != null) result.add(item.toString());
                }
                return result;
            }
        } catch (Exception e) {
            log.debug("Could not parse acceptance_criteria JSON: {}", e.getMessage());
        }
        return List.of();
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
