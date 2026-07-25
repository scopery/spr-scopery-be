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
public class AddFunctionalItemPropertiesToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(AddFunctionalItemPropertiesToolAdapter.class);

    private static final String TOOL_CODE = "add_functional_item_properties";
    private static final String TOOL_VERSION = "v1";

    private final FunctionalItemRepository functionalItemRepository;
    private final FunctionalItemCustomPropertyRepository customPropertyRepository;

    public AddFunctionalItemPropertiesToolAdapter(FunctionalItemRepository functionalItemRepository,
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
        return "Add custom metadata properties to an existing functional item. "
                + "Required: itemId (UUID of the functional item), projectId, properties (array of {propKey, propValue, fieldType}). "
                + "fieldType options: TEXT, NUMBER, DATE, BOOLEAN, URL. Skips duplicate keys with a warning.";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "itemId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "UUID of the functional item to add properties to."
                    },
                    "projectId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "UUID of the project."
                    },
                    "properties": {
                      "type": "array",
                      "description": "List of custom properties to add.",
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
                  "required": ["itemId", "projectId", "properties"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String itemId = getString(inputArgs, "itemId");
        if (itemId != null) hints.put("itemId", itemId);
        Object props = inputArgs.get("properties");
        if (props instanceof List<?> list) hints.put("propertyCount", String.valueOf(list.size()));
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String itemId = getString(input, "itemId");
        if (itemId == null) {
            return new AiActionDryRunResult(false, List.of("Missing required field: itemId"), null, false, null);
        }
        List<Map<String, Object>> props = parseMapList(input.get("properties"));
        if (props.isEmpty()) {
            return new AiActionDryRunResult(false, List.of("properties array is empty"), null, false, null);
        }
        StringBuilder diff = new StringBuilder();
        diff.append("{\"itemId\":\"").append(itemId).append("\",\"properties\":[");
        for (int i = 0; i < props.size(); i++) {
            Map<String, Object> p = props.get(i);
            diff.append("{\"propKey\":\"").append(getString(p, "propKey"))
                    .append("\",\"propValue\":\"").append(getString(p, "propValue"))
                    .append("\",\"fieldType\":\"").append(getString(p, "fieldType") != null ? getString(p, "fieldType") : "TEXT")
                    .append("\"}");
            if (i < props.size() - 1) diff.append(",");
        }
        diff.append("]}");
        return new AiActionDryRunResult(true, List.of(), null, false, diff.toString());
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String itemIdStr = getString(input, "itemId");
        if (itemIdStr == null) {
            return AiActionToolResult.failed("MISSING_ITEM_ID", false);
        }

        UUID itemId;
        try {
            itemId = UUID.fromString(itemIdStr);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_ITEM_ID_UUID", false);
        }

        String projectIdStr = getString(input, "projectId");
        if (projectIdStr != null) {
            try {
                UUID projectId = UUID.fromString(projectIdStr);
                if (functionalItemRepository.findByIdAndProjectId(itemId, projectId).isEmpty()) {
                    return AiActionToolResult.failed("FUNCTIONAL_ITEM_NOT_FOUND", false);
                }
            } catch (IllegalArgumentException e) {
                return AiActionToolResult.failed("INVALID_PROJECT_ID_UUID", false);
            }
        }

        List<Map<String, Object>> propsInput = parseMapList(input.get("properties"));
        if (propsInput.isEmpty()) {
            return AiActionToolResult.failed("EMPTY_PROPERTIES", false);
        }

        List<String> createdKeys = new ArrayList<>();
        for (Map<String, Object> propMap : propsInput) {
            String propKey = getString(propMap, "propKey");
            if (propKey == null || propKey.isBlank()) continue;
            if (customPropertyRepository.existsByFunctionalItemIdAndPropKey(itemId, propKey)) {
                log.warn("[AddFunctionalItemPropertiesTool] propKey '{}' already exists on item {}, skipping", propKey, itemId);
                continue;
            }
            String propValue = getString(propMap, "propValue");
            String fieldTypeStr = getString(propMap, "fieldType");
            CustomPropertyFieldType fieldType = CustomPropertyFieldType.TEXT;
            if (fieldTypeStr != null) {
                try { fieldType = CustomPropertyFieldType.valueOf(fieldTypeStr.toUpperCase()); }
                catch (IllegalArgumentException ignored) { /* default TEXT */ }
            }
            customPropertyRepository.save(FunctionalItemCustomProperty.create(itemId, propKey, propValue, fieldType));
            createdKeys.add(propKey);
        }

        log.info("[AddFunctionalItemPropertiesTool] Added {} properties to item={}", createdKeys.size(), itemId);

        String resultSummary = "{\"itemId\":\"" + itemId + "\",\"addedCount\":" + createdKeys.size() + "}";
        return AiActionToolResult.succeeded(itemId + ":props:v1", resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
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

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
