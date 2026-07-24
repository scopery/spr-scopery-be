package com.company.scopery.modules.documenthub.generatedjob.application.action;

import com.company.scopery.common.exception.BusinessException;
import com.company.scopery.common.exception.NotFoundException;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiassistant.application.service.ProjectContextSnapshotService;
import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfig;
import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfigRepository;
import com.company.scopery.modules.documenthub.generatedjob.application.response.ProjectBriefPreviewResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class GenerateProjectBriefAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateProjectBriefAction.class);

    private static final String PROJECT_SQL = """
            SELECT name, code, status, description,
                   planned_start_date::text AS start_date,
                   planned_end_date::text   AS end_date,
                   workspace_id
            FROM project_project
            WHERE id = :projectId::uuid
            LIMIT 1
            """;

    private static final String DEFAULT_DEPLOYMENT_SQL = """
            SELECT p.id AS provider_id, m.provider_model_id, p.code AS provider_code
            FROM aiagent_model_deployment d
            JOIN aiagent_model m ON m.id = d.model_id
            JOIN aiagent_provider p ON p.id = m.provider_id
            WHERE d.is_default = true AND d.status = 'ACTIVE'
            LIMIT 1
            """;

    private static final String PROMPT_TEMPLATE = """
            You are a project management AI. Generate a concise project summary in valid JSON only (no markdown, no explanation).

            Project:
            - Name: {name}
            - Code: {code}
            - Status: {status}
            - Description: {description}
            - Planned: {startDate} → {endDate}

            {snapshot}

            Respond with ONLY this JSON structure:
            {
              "title": "Project Summary: {name}",
              "sections": [
                {"heading": "Overview", "body": "<2-3 sentence summary>"},
                {"heading": "Current Status", "bullets": ["<key status point>"]},
                {"heading": "Key Concerns", "bullets": ["<risk or issue>"]},
                {"heading": "Recommended Next Steps", "bullets": ["<action>"]}\s
              ],
              "assumptions": []
            }
            """;

    private final NamedParameterJdbcTemplate jdbc;
    private final AiAssistantWorkspaceConfigRepository configRepository;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final ProjectContextSnapshotService snapshotService;
    private final ObjectMapper objectMapper;

    public GenerateProjectBriefAction(
            NamedParameterJdbcTemplate jdbc,
            AiAssistantWorkspaceConfigRepository configRepository,
            AiProviderAdapterRegistry adapterRegistry,
            ProjectContextSnapshotService snapshotService,
            ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.configRepository = configRepository;
        this.adapterRegistry = adapterRegistry;
        this.snapshotService = snapshotService;
        this.objectMapper = objectMapper;
    }

    public ProjectBriefPreviewResponse execute(UUID projectId) {
        Map<String, Object> row = fetchProject(projectId);
        UUID workspaceId = (UUID) row.get("workspace_id");

        String providerCode;
        String modelName;
        UUID providerId;
        BigDecimal temperature = new BigDecimal("0.3");
        Integer maxTokens = 1200;

        Optional<AiAssistantWorkspaceConfig> wsConfig = configRepository.findByWorkspaceId(workspaceId);
        if (wsConfig.isPresent() && wsConfig.get().modelProvider() != null && wsConfig.get().modelName() != null) {
            AiAssistantWorkspaceConfig cfg = wsConfig.get();
            providerCode = cfg.modelProvider();
            modelName = cfg.modelName();
            providerId = null; // workspace config doesn't store providerId directly
            if (cfg.temperatureOverride() != null) temperature = cfg.temperatureOverride();
            if (cfg.maxOutputTokensOverride() != null) maxTokens = cfg.maxOutputTokensOverride();
        } else {
            Map<String, Object> deployment = resolveDefaultDeployment();
            providerCode = (String) deployment.get("provider_code");
            modelName = (String) deployment.get("provider_model_id");
            providerId = null; // let adapter fall back to env API key
        }

        String snapshot = snapshotService.getSnapshot(projectId);
        String prompt = buildPrompt(row, snapshot);

        AiProviderAdapter adapter = adapterRegistry.getAdapter(providerCode);

        AiProviderResponse response = adapter.call(
                new AiProviderRequest(providerId, modelName, prompt, temperature, maxTokens));

        String rawText = response.outputText() != null ? response.outputText().strip() : "";
        ProjectBriefPreviewResponse.Preview preview = parsePreview(rawText, (String) row.get("name"));

        return new ProjectBriefPreviewResponse(preview, null, Collections.emptyList());
    }

    private Map<String, Object> fetchProject(UUID projectId) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                PROJECT_SQL, new MapSqlParameterSource("projectId", projectId.toString()));
        if (rows.isEmpty()) {
            throw new NotFoundException("Project not found: " + projectId);
        }
        return rows.get(0);
    }

    private Map<String, Object> resolveDefaultDeployment() {
        List<Map<String, Object>> rows = jdbc.queryForList(DEFAULT_DEPLOYMENT_SQL, new MapSqlParameterSource());
        if (rows.isEmpty()) {
            throw new BusinessException("No active AI model deployment found. Please configure an AI provider.");
        }
        return rows.get(0);
    }

    private String buildPrompt(Map<String, Object> row, String snapshot) {
        return PROMPT_TEMPLATE
                .replace("{name}", str(row.get("name")))
                .replace("{code}", str(row.get("code")))
                .replace("{status}", str(row.get("status")))
                .replace("{description}", str(row.get("description")))
                .replace("{startDate}", str(row.get("start_date")))
                .replace("{endDate}", str(row.get("end_date")))
                .replace("{snapshot}", snapshot);
    }

    private ProjectBriefPreviewResponse.Preview parsePreview(String rawText, String projectName) {
        try {
            String json = extractJson(rawText);
            JsonNode root = objectMapper.readTree(json);

            String title = root.path("title").asText("Project Summary: " + projectName);

            List<ProjectBriefPreviewResponse.Section> sections = new ArrayList<>();
            JsonNode sectionsNode = root.path("sections");
            if (sectionsNode.isArray()) {
                for (JsonNode s : sectionsNode) {
                    String heading = s.path("heading").asText(null);
                    String body = s.path("body").asText(null);
                    List<String> bullets = new ArrayList<>();
                    JsonNode bulletsNode = s.path("bullets");
                    if (bulletsNode.isArray()) {
                        for (JsonNode b : bulletsNode) bullets.add(b.asText());
                    }
                    sections.add(new ProjectBriefPreviewResponse.Section(
                            heading,
                            (body != null && !body.isBlank()) ? body : null,
                            bullets.isEmpty() ? null : bullets
                    ));
                }
            }

            List<String> assumptions = new ArrayList<>();
            JsonNode assumptionsNode = root.path("assumptions");
            if (assumptionsNode.isArray()) {
                for (JsonNode a : assumptionsNode) assumptions.add(a.asText());
            }

            return new ProjectBriefPreviewResponse.Preview(title, sections, assumptions.isEmpty() ? null : assumptions);
        } catch (Exception e) {
            log.warn("[ProjectBrief] Failed to parse AI JSON response, falling back to raw text: {}", e.getMessage());
            return fallbackPreview(rawText, projectName);
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return text;
    }

    private ProjectBriefPreviewResponse.Preview fallbackPreview(String rawText, String projectName) {
        List<ProjectBriefPreviewResponse.Section> sections = List.of(
                new ProjectBriefPreviewResponse.Section("Summary", rawText, null));
        return new ProjectBriefPreviewResponse.Preview("Project Summary: " + projectName, sections, null);
    }

    private static String str(Object val) {
        return val != null ? val.toString() : "—";
    }
}
