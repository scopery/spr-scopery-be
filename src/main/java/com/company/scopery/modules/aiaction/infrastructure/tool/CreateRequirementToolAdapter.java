package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.traceability.requirement.domain.enums.RequirementPriority;
import com.company.scopery.modules.traceability.requirement.domain.enums.RequirementType;
import com.company.scopery.modules.traceability.requirement.domain.model.Requirement;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateRequirementToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateRequirementToolAdapter.class);
    private static final String TOOL_CODE = "create_requirement";
    private static final String TOOL_VERSION = "v1";

    private final RequirementRepository requirementRepository;

    public CreateRequirementToolAdapter(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    @Override public String toolCode() { return TOOL_CODE; }
    @Override public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new project requirement. "
                + "Required: projectId, workspaceId, title, requirementType (FR/NFR/BR/BO), priority (LOW/MEDIUM/HIGH/CRITICAL). "
                + "Optional: code, description, functionalItemId (UUID of linked FR), nonFunctionalItemId, scopeItemId (UUID of linked scope item). "
                + "FIELD COMPLETENESS rules: "
                + "(1) description: always include a full explanation of what this requirement entails and why it is needed. "
                + "(2) requirementType: FR for functional requirements, NFR for non-functional, BR for business rules, BO for business objectives. "
                + "(3) priority: infer from context or default to MEDIUM. "
                + "(4) code: auto-generated if omitted (format REQ-XXXXXX).";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "projectId":          { "type": "string", "format": "uuid" },
                    "workspaceId":        { "type": "string", "format": "uuid" },
                    "title":              { "type": "string" },
                    "requirementType":    { "type": "string", "enum": ["FR", "NFR", "BR", "BO"] },
                    "priority":           { "type": "string", "enum": ["LOW", "MEDIUM", "HIGH", "CRITICAL"] },
                    "code":               { "type": "string" },
                    "description":        { "type": "string" },
                    "functionalItemId":   { "type": "string", "format": "uuid" },
                    "nonFunctionalItemId":{ "type": "string", "format": "uuid" },
                    "scopeItemId":        { "type": "string", "format": "uuid" }
                  },
                  "required": ["projectId", "workspaceId", "title", "requirementType", "priority"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        var hints = new java.util.LinkedHashMap<String, String>();
        String title = getString(inputArgs, "title");
        if (title != null) hints.put("title", title);
        String type = getString(inputArgs, "requirementType");
        if (type != null) hints.put("requirementType", type);
        String priority = getString(inputArgs, "priority");
        if (priority != null) hints.put("priority", priority);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String projectId = getString(input, "projectId");
        String title = getString(input, "title");
        String type = getString(input, "requirementType");
        String priority = getString(input, "priority");
        if (projectId == null || title == null || title.isBlank() || type == null || priority == null) {
            return new AiActionDryRunResult(false, List.of("Missing required: projectId, title, requirementType, priority"), null, false, null);
        }
        String diff = "{\"projectId\":\"" + projectId + "\",\"title\":\"" + title
                + "\",\"requirementType\":\"" + type + "\",\"priority\":\"" + priority + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diff);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String projectId = getString(input, "projectId");
        String workspaceId = getString(input, "workspaceId");
        String title = getString(input, "title");
        String typeStr = getString(input, "requirementType");
        String priorityStr = getString(input, "priority");

        if (projectId == null || workspaceId == null || title == null || title.isBlank() || typeStr == null || priorityStr == null) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID projectUuid, workspaceUuid;
        try {
            projectUuid = UUID.fromString(projectId);
            workspaceUuid = UUID.fromString(workspaceId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
        }

        RequirementType type;
        RequirementPriority priority;
        try {
            type = RequirementType.valueOf(typeStr.toUpperCase());
            priority = RequirementPriority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_ENUM_VALUE", false);
        }

        String description = getString(input, "description");
        String code = getString(input, "code");
        if (code == null || code.isBlank()) {
            code = generateUniqueCode(projectUuid);
        }

        UUID functionalItemId = parseUuid(getString(input, "functionalItemId"));
        UUID nonFunctionalItemId = parseUuid(getString(input, "nonFunctionalItemId"));
        UUID scopeItemId = parseUuid(getString(input, "scopeItemId"));

        Requirement saved = requirementRepository.save(
                Requirement.create(projectUuid, workspaceUuid, null, code, title.trim(), description,
                        type, priority, functionalItemId, nonFunctionalItemId, scopeItemId));

        log.info("[CreateRequirementTool] created — id={} code={} project={}", saved.id(), saved.code(), projectUuid);

        String resultSummary = "{\"requirementId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                + "\",\"title\":\"" + saved.title() + "\",\"type\":\"" + saved.requirementType() + "\"}";
        return AiActionToolResult.succeeded(saved.id() + ":v1", resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private String generateUniqueCode(UUID projectId) {
        for (int i = 0; i < 10; i++) {
            String candidate = "REQ-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            if (!requirementRepository.existsByProjectIdAndCode(projectId, candidate)) return candidate;
        }
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }

    private static UUID parseUuid(String val) {
        if (val == null) return null;
        try { return UUID.fromString(val); } catch (IllegalArgumentException e) { return null; }
    }
}
