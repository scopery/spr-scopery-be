package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrCategory;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrPriority;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrScopeType;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItem;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateNonFunctionalItemToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateNonFunctionalItemToolAdapter.class);

    private static final String TOOL_CODE = "create_non_functional_item";
    private static final String TOOL_VERSION = "v1";

    private final NonFunctionalItemRepository nfrRepository;

    public CreateNonFunctionalItemToolAdapter(NonFunctionalItemRepository nfrRepository) {
        this.nfrRepository = nfrRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new NFR catalog item — a non-functional quality attribute (performance, security, usability, etc.) in the NFR Catalog module. "
                + "USE THIS TOOL when the user explicitly says: 'NFR', 'non-functional', 'phi chức năng', 'chất lượng hệ thống', or names a quality category (performance, security, usability). "
                + "DO NOT use this tool when the user says 'requirement'/'yêu cầu' without specifying non-functional — use create_requirement instead. "
                + "Required: projectId, workspaceId, title, category (PERFORMANCE/SECURITY/USABILITY/RELIABILITY/MAINTAINABILITY/SCALABILITY/COMPATIBILITY/OTHER). "
                + "Optional: description, priority (LOW/MEDIUM/HIGH/CRITICAL), targetMetric, scopeType (SYSTEM/MODULE/FEATURE), scopeRefId. "
                + "FIELD COMPLETENESS — always follow these rules: "
                + "(1) description: always include a clear description of the quality attribute it enforces and why it matters. Never leave empty. "
                + "(2) targetMetric: always include a measurable target when the context provides one (e.g. 'response time < 200ms', '99.9% uptime', 'WCAG 2.1 AA'). "
                + "(3) priority: infer from user language or default to MEDIUM. "
                + "(4) scopeType: set to SYSTEM unless the user specifies a specific module or feature.";
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
                    "workspaceId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the workspace."
                    },
                    "title": {
                      "type": "string",
                      "description": "The title of the NFR."
                    },
                    "category": {
                      "type": "string",
                      "enum": ["PERFORMANCE", "SECURITY", "USABILITY", "RELIABILITY", "MAINTAINABILITY", "SCALABILITY", "COMPATIBILITY", "OTHER"],
                      "description": "The NFR category."
                    },
                    "description": {
                      "type": "string",
                      "description": "Optional description."
                    },
                    "priority": {
                      "type": "string",
                      "enum": ["LOW", "MEDIUM", "HIGH", "CRITICAL"],
                      "description": "Priority. Defaults to MEDIUM."
                    },
                    "targetMetric": {
                      "type": "string",
                      "description": "Optional target metric (e.g. 'response time < 200ms')."
                    },
                    "scopeType": {
                      "type": "string",
                      "enum": ["SYSTEM", "MODULE", "FEATURE"],
                      "description": "Scope type. Defaults to SYSTEM."
                    },
                    "scopeRefId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "Optional reference ID for the scope (module or feature UUID)."
                    }
                  },
                  "required": ["projectId", "workspaceId", "title", "category"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String title = getString(inputArgs, "title");
        if (title != null) hints.put("title", title);
        String category = getString(inputArgs, "category");
        if (category != null) hints.put("category", category);
        String priority = getString(inputArgs, "priority");
        if (priority != null) hints.put("priority", priority);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String projectId = getString(input, "projectId");
        String workspaceId = getString(input, "workspaceId");
        String title = getString(input, "title");
        String category = getString(input, "category");
        if (projectId == null || workspaceId == null || title == null || title.isBlank() || category == null) {
            return new AiActionDryRunResult(false, List.of("Missing required field: projectId, workspaceId, title, or category"), null, false, null);
        }
        String diffJson = "{\"projectId\":\"" + projectId + "\",\"title\":\"" + title + "\",\"category\":\"" + category + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String projectId = getString(input, "projectId");
        String workspaceId = getString(input, "workspaceId");
        String title = getString(input, "title");
        String categoryStr = getString(input, "category");

        if (projectId == null || workspaceId == null || title == null || title.isBlank() || categoryStr == null) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID projectUuid;
        UUID workspaceUuid;
        try {
            projectUuid = UUID.fromString(projectId);
            workspaceUuid = UUID.fromString(workspaceId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
        }

        NfrCategory category;
        try {
            category = NfrCategory.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_NFR_CATEGORY", false);
        }

        String priorityStr = getString(input, "priority");
        NfrPriority priority = priorityStr != null ? parseOrDefaultPriority(priorityStr) : NfrPriority.MEDIUM;

        String scopeTypeStr = getString(input, "scopeType");
        NfrScopeType scopeType = scopeTypeStr != null ? parseOrDefaultScopeType(scopeTypeStr) : NfrScopeType.SYSTEM;

        UUID scopeRefId = null;
        String scopeRefIdStr = getString(input, "scopeRefId");
        if (scopeRefIdStr != null) {
            try {
                scopeRefId = UUID.fromString(scopeRefIdStr);
            } catch (IllegalArgumentException e) {
                log.warn("[CreateNFRTool] Invalid scopeRefId '{}', ignoring", scopeRefIdStr);
            }
        }

        String description = getString(input, "description");
        String targetMetric = getString(input, "targetMetric");
        String code = generateUniqueCode(projectUuid);

        NonFunctionalItem item = NonFunctionalItem.create(projectUuid, workspaceUuid, code, title,
                description, category, priority, targetMetric, scopeType, scopeRefId);
        NonFunctionalItem saved = nfrRepository.save(item);

        log.info("[CreateNFRTool] NFR created — id={} code={} project={}", saved.id(), saved.code(), projectUuid);

        String resultToken = saved.id() + ":v1";
        String resultSummary = "{\"nfrId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                + "\",\"title\":\"" + saved.title() + "\",\"category\":\"" + saved.category() + "\"}";

        return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private String generateUniqueCode(UUID projectId) {
        for (int i = 0; i < 10; i++) {
            String candidate = "NFR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            if (!nfrRepository.existsByProjectIdAndCode(projectId, candidate)) {
                return candidate;
            }
        }
        return "NFR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static NfrPriority parseOrDefaultPriority(String value) {
        try {
            return NfrPriority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NfrPriority.MEDIUM;
        }
    }

    private static NfrScopeType parseOrDefaultScopeType(String value) {
        try {
            return NfrScopeType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NfrScopeType.SYSTEM;
        }
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
