package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateRegistryDataEntityToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateRegistryDataEntityToolAdapter.class);

    private static final String TOOL_CODE = "create_data_entity";
    private static final String TOOL_VERSION = "v1";

    private final RegistryDataEntityRepository dataEntityRepository;

    public CreateRegistryDataEntityToolAdapter(RegistryDataEntityRepository dataEntityRepository) {
        this.dataEntityRepository = dataEntityRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new data entity in the registry. Use when the user asks to add or create a new data entity (database table/domain object) for an application. "
                + "Required: applicationId, workspaceId, and name. Optional: moduleId, description, tableName.";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "applicationId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the application to add the data entity to."
                    },
                    "workspaceId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the workspace."
                    },
                    "name": {
                      "type": "string",
                      "description": "The name of the data entity."
                    },
                    "moduleId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "Optional UUID of the module this entity belongs to."
                    },
                    "description": {
                      "type": "string",
                      "description": "Optional description of the data entity."
                    },
                    "tableName": {
                      "type": "string",
                      "description": "Optional database table name for this entity."
                    }
                  },
                  "required": ["applicationId", "workspaceId", "name"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String name = getString(inputArgs, "name");
        if (name != null) hints.put("name", name);
        String tableName = getString(inputArgs, "tableName");
        if (tableName != null) hints.put("tableName", tableName);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String applicationId = getString(input, "applicationId");
        String workspaceId = getString(input, "workspaceId");
        String name = getString(input, "name");
        if (applicationId == null || workspaceId == null || name == null || name.isBlank()) {
            return new AiActionDryRunResult(false, List.of("Missing required field: applicationId, workspaceId, or name"), null, false, null);
        }
        try {
            UUID.fromString(applicationId);
            UUID.fromString(workspaceId);
        } catch (IllegalArgumentException e) {
            return new AiActionDryRunResult(false, List.of("Invalid UUID format for applicationId or workspaceId"), null, false, null);
        }
        String moduleId = getString(input, "moduleId");
        if (moduleId != null) {
            try {
                UUID.fromString(moduleId);
            } catch (IllegalArgumentException e) {
                return new AiActionDryRunResult(false, List.of("Invalid UUID format for moduleId"), null, false, null);
            }
        }
        String diffJson = "{\"applicationId\":\"" + applicationId + "\",\"name\":\"" + name + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String applicationId = getString(input, "applicationId");
        String workspaceId = getString(input, "workspaceId");
        String name = getString(input, "name");

        if (applicationId == null || workspaceId == null || name == null || name.isBlank()) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID applicationUuid;
        UUID workspaceUuid;
        try {
            applicationUuid = UUID.fromString(applicationId);
            workspaceUuid = UUID.fromString(workspaceId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
        }

        String moduleIdStr = getString(input, "moduleId");
        UUID moduleUuid = null;
        if (moduleIdStr != null) {
            try {
                moduleUuid = UUID.fromString(moduleIdStr);
            } catch (IllegalArgumentException e) {
                return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
            }
        }

        String code = generateCode(name);
        String description = getString(input, "description");
        String tableName = getString(input, "tableName");

        RegistryDataEntity entity = RegistryDataEntity.create(applicationUuid, workspaceUuid, moduleUuid, code, name, description, tableName);
        RegistryDataEntity saved = dataEntityRepository.save(entity);

        log.info("[CreateDataEntityTool] Data entity created — id={} code={} application={}", saved.id(), saved.code(), applicationUuid);

        String resultToken = saved.id() + ":v1";
        String resultSummary = "{\"entityId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                + "\",\"name\":\"" + saved.name() + "\"}";

        return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private String generateCode(String name) {
        if (name != null && !name.isBlank()) {
            String slug = name.trim().toUpperCase().replaceAll("[^A-Z0-9]", "-").replaceAll("-+", "-");
            return "ENT-" + slug;
        }
        return "ENT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
