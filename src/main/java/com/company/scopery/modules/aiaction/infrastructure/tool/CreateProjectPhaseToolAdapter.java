package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateProjectPhaseToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateProjectPhaseToolAdapter.class);

    private static final String TOOL_CODE = "create_project_phase";
    private static final String TOOL_VERSION = "v1";

    private final ProjectPhaseRepository projectPhaseRepository;

    public CreateProjectPhaseToolAdapter(ProjectPhaseRepository projectPhaseRepository) {
        this.projectPhaseRepository = projectPhaseRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new project phase. Use when the user asks to add, create, or define a new phase in a project. "
                + "Required: projectId and name. Optional: description, displayOrder, plannedStartDate (YYYY-MM-DD), plannedEndDate (YYYY-MM-DD).";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "projectId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the project."
                    },
                    "name": {
                      "type": "string",
                      "description": "The name of the phase."
                    },
                    "description": {
                      "type": "string",
                      "description": "Optional description of the phase."
                    },
                    "displayOrder": {
                      "type": "integer",
                      "description": "Display order of the phase. Defaults to next available order if not provided."
                    },
                    "plannedStartDate": {
                      "type": "string",
                      "format": "date",
                      "description": "Planned start date in YYYY-MM-DD format. Optional."
                    },
                    "plannedEndDate": {
                      "type": "string",
                      "format": "date",
                      "description": "Planned end date in YYYY-MM-DD format. Optional."
                    }
                  },
                  "required": ["projectId", "name"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String name = getString(inputArgs, "name");
        if (name != null) hints.put("name", name);
        String description = getString(inputArgs, "description");
        if (description != null) hints.put("description", description);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String projectId = getString(input, "projectId");
        String name = getString(input, "name");
        if (projectId == null || name == null || name.isBlank()) {
            return new AiActionDryRunResult(false, List.of("Missing required field: projectId or name"), null, false, null);
        }
        try {
            UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return new AiActionDryRunResult(false, List.of("Invalid projectId format"), null, false, null);
        }
        String diffJson = "{\"projectId\":\"" + projectId + "\",\"name\":\"" + name + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String projectId = getString(input, "projectId");
        String name = getString(input, "name");

        if (projectId == null || name == null || name.isBlank()) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID projectUuid;
        try {
            projectUuid = UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_PROJECT_ID", false);
        }

        String code = generateUniqueCode(projectUuid);
        String description = getString(input, "description");

        int displayOrder = resolveDisplayOrder(projectUuid, input);

        LocalDate plannedStartDate = parseDate(getString(input, "plannedStartDate"));
        LocalDate plannedEndDate = parseDate(getString(input, "plannedEndDate"));

        ProjectPhase phase = ProjectPhase.create(projectUuid, code, name, description, displayOrder, plannedStartDate, plannedEndDate);
        ProjectPhase saved = projectPhaseRepository.save(phase);

        log.info("[CreateProjectPhaseTool] Phase created — id={} code={} project={}", saved.id(), saved.code(), projectUuid);

        String resultToken = saved.id() + ":v1";
        String resultSummary = "{\"phaseId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                + "\",\"name\":\"" + saved.name() + "\"}";

        return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private int resolveDisplayOrder(UUID projectId, Map<String, Object> input) {
        Object raw = input.get("displayOrder");
        if (raw != null) {
            try {
                int order = Integer.parseInt(raw.toString());
                if (order > 0 && !projectPhaseRepository.existsByProjectIdAndDisplayOrder(projectId, order)) {
                    return order;
                }
            } catch (NumberFormatException ignored) {}
        }
        List<com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase> existing =
                projectPhaseRepository.findAllByProjectId(projectId);
        return existing.size() + 1;
    }

    private String generateUniqueCode(UUID projectId) {
        for (int i = 0; i < 10; i++) {
            String candidate = "PH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            if (!projectPhaseRepository.existsByProjectIdAndCode(projectId, candidate)) {
                return candidate;
            }
        }
        return "PH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private LocalDate parseDate(String value) {
        if (value == null) return null;
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            log.warn("[CreateProjectPhaseTool] Invalid date '{}', ignoring", value);
            return null;
        }
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
