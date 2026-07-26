package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.traceability.funcitemprop.domain.enums.CustomPropertyFieldType;
import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomProperty;
import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomPropertyRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemPriority;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemType;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateFunctionalItemToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateFunctionalItemToolAdapter.class);

    private static final String TOOL_CODE = "create_functional_item";
    private static final String TOOL_VERSION = "v1";

    private final FunctionalItemRepository functionalItemRepository;
    private final FunctionalItemCustomPropertyRepository customPropertyRepository;

    public CreateFunctionalItemToolAdapter(FunctionalItemRepository functionalItemRepository,
                                           FunctionalItemCustomPropertyRepository customPropertyRepository) {
        this.functionalItemRepository = functionalItemRepository;
        this.customPropertyRepository = customPropertyRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new functional catalog item — a feature specification, user story, or use case in the Functional Catalog module. "
                + "USE THIS TOOL when the user explicitly says: 'functional item', 'chức năng', 'feature spec', 'user story', 'use case', or refers to the Functional Catalog. "
                + "DO NOT use this tool when the user says 'requirement', 'yêu cầu', 'tạo yêu cầu' — use create_requirement instead. "
                + "Required: projectId, workspaceId, title, type (FUNCTIONAL/USER_STORY/USE_CASE). "
                + "Optional: description, moduleId, priority (LOW/MEDIUM/HIGH/CRITICAL), acceptanceCriteria (array of strings), "
                + "customProperties (array of {propKey, propValue, fieldType: TEXT|NUMBER|DATE|BOOLEAN|URL}). "
                + "FIELD COMPLETENESS — always follow these rules: "
                + "(1) description: always include a full description explaining what the feature does, why it is needed, and who benefits. Never leave empty. "
                + "(2) acceptanceCriteria: always derive at least 2–4 concrete, testable criteria from the user's description or project context. Use plain sentences or Given/When/Then format. Never omit. "
                + "(3) priority: infer from user language or default to MEDIUM. "
                + "(4) customProperties: if the user mentions any attributes, fields, or metadata (e.g. complexity, screen, actor, owner, business impact), include them here. "
                + "To add properties to an existing item, use add_functional_item_properties instead.";
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
                      "description": "The title of the functional item."
                    },
                    "type": {
                      "type": "string",
                      "enum": ["FUNCTIONAL", "USER_STORY", "USE_CASE"],
                      "description": "The type of functional item."
                    },
                    "description": {
                      "type": "string",
                      "description": "Optional description."
                    },
                    "moduleId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "Optional UUID of the module this item belongs to."
                    },
                    "priority": {
                      "type": "string",
                      "enum": ["LOW", "MEDIUM", "HIGH", "CRITICAL"],
                      "description": "Priority. Defaults to MEDIUM."
                    },
                    "acceptanceCriteria": {
                      "type": "array",
                      "items": { "type": "string" },
                      "description": "Optional list of acceptance criteria."
                    },
                    "customProperties": {
                      "type": "array",
                      "description": "Optional custom metadata properties to attach to this item.",
                      "items": {
                        "type": "object",
                        "properties": {
                          "propKey": { "type": "string", "description": "Unique key, e.g. 'complexity'" },
                          "propValue": { "type": "string", "description": "The value, e.g. 'High'" },
                          "fieldType": {
                            "type": "string",
                            "enum": ["TEXT", "NUMBER", "DATE", "BOOLEAN", "URL"],
                            "description": "Data type. Defaults to TEXT."
                          }
                        },
                        "required": ["propKey"]
                      }
                    }
                  },
                  "required": ["projectId", "workspaceId", "title", "type"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String title = getString(inputArgs, "title");
        if (title != null) hints.put("title", title);
        String type = getString(inputArgs, "type");
        if (type != null) hints.put("type", type);
        String priority = getString(inputArgs, "priority");
        if (priority != null) hints.put("priority", priority);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String projectId = getString(input, "projectId");
        String workspaceId = getString(input, "workspaceId");
        String title = getString(input, "title");
        String type = getString(input, "type");
        if (projectId == null || workspaceId == null || title == null || title.isBlank() || type == null) {
            return new AiActionDryRunResult(false, List.of("Missing required field: projectId, workspaceId, title, or type"), null, false, null);
        }
        List<Map<String, Object>> props = parseMapList(input.get("customProperties"));
        StringBuilder diff = new StringBuilder();
        diff.append("{\"projectId\":\"").append(projectId)
                .append("\",\"title\":\"").append(title)
                .append("\",\"type\":\"").append(type).append("\"");
        if (!props.isEmpty()) {
            diff.append(",\"customProperties\":[");
            for (int i = 0; i < props.size(); i++) {
                Map<String, Object> p = props.get(i);
                diff.append("{\"propKey\":\"").append(getString(p, "propKey"))
                        .append("\",\"propValue\":\"").append(getString(p, "propValue"))
                        .append("\",\"fieldType\":\"").append(getString(p, "fieldType") != null ? getString(p, "fieldType") : "TEXT")
                        .append("\"}");
                if (i < props.size() - 1) diff.append(",");
            }
            diff.append("]");
        }
        diff.append("}");
        return new AiActionDryRunResult(true, List.of(), null, false, diff.toString());
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String projectId = getString(input, "projectId");
        String workspaceId = getString(input, "workspaceId");
        String title = getString(input, "title");
        String typeStr = getString(input, "type");

        if (projectId == null || workspaceId == null || title == null || title.isBlank() || typeStr == null) {
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

        FunctionalItemType type;
        try {
            type = FunctionalItemType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_FUNCTIONAL_ITEM_TYPE", false);
        }

        UUID moduleUuid = null;
        String moduleIdStr = getString(input, "moduleId");
        if (moduleIdStr != null) {
            try {
                moduleUuid = UUID.fromString(moduleIdStr);
            } catch (IllegalArgumentException e) {
                log.warn("[CreateFunctionalItemTool] Invalid moduleId '{}', ignoring", moduleIdStr);
            }
        }

        String priorityStr = getString(input, "priority");
        FunctionalItemPriority priority = priorityStr != null ? parseOrDefault(priorityStr) : FunctionalItemPriority.MEDIUM;

        List<String> acceptanceCriteria = parseStringList(input.get("acceptanceCriteria"));
        String description = getString(input, "description");

        String code = generateUniqueCode(projectUuid);

        FunctionalItem item = FunctionalItem.create(projectUuid, workspaceUuid, moduleUuid,
                code, title, description, priority, type, acceptanceCriteria);
        FunctionalItem saved = functionalItemRepository.save(item);

        List<Map<String, Object>> propsInput = parseMapList(input.get("customProperties"));
        List<String> createdPropKeys = new ArrayList<>();
        for (Map<String, Object> propMap : propsInput) {
            String propKey = getString(propMap, "propKey");
            if (propKey == null || propKey.isBlank()) continue;
            if (customPropertyRepository.existsByFunctionalItemIdAndPropKey(saved.id(), propKey)) {
                log.warn("[CreateFunctionalItemTool] propKey '{}' already exists, skipping", propKey);
                continue;
            }
            String propValue = getString(propMap, "propValue");
            String fieldTypeStr = getString(propMap, "fieldType");
            CustomPropertyFieldType fieldType = CustomPropertyFieldType.TEXT;
            if (fieldTypeStr != null) {
                try { fieldType = CustomPropertyFieldType.valueOf(fieldTypeStr.toUpperCase()); }
                catch (IllegalArgumentException ignored) { /* default TEXT */ }
            }
            customPropertyRepository.save(FunctionalItemCustomProperty.create(saved.id(), propKey, propValue, fieldType));
            createdPropKeys.add(propKey);
        }

        log.info("[CreateFunctionalItemTool] FR created — id={} code={} project={} props={}", saved.id(), saved.code(), projectUuid, createdPropKeys.size());

        String resultToken = saved.id() + ":v1";
        String resultSummary = "{\"itemId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                + "\",\"title\":\"" + saved.title() + "\",\"type\":\"" + saved.type()
                + "\",\"customPropertiesCreated\":" + createdPropKeys.size() + "}";

        return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private String generateUniqueCode(UUID projectId) {
        for (int i = 0; i < 10; i++) {
            String candidate = "FR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            if (!functionalItemRepository.existsByProjectIdAndCode(projectId, candidate)) {
                return candidate;
            }
        }
        return "FR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseMapList(Object raw) {
        if (raw instanceof List<?> list) {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    result.add((Map<String, Object>) m);
                }
            }
            return result;
        }
        return List.of();
    }

    private List<String> parseStringList(Object raw) {
        if (raw instanceof List<?> list) {
            return list.stream()
                    .filter(o -> o != null)
                    .map(Object::toString)
                    .toList();
        }
        return List.of();
    }

    private static FunctionalItemPriority parseOrDefault(String value) {
        try {
            return FunctionalItemPriority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FunctionalItemPriority.MEDIUM;
        }
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
