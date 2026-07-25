package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateRegistryScreenToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateRegistryScreenToolAdapter.class);

    private static final String TOOL_CODE = "create_screen";
    private static final String TOOL_VERSION = "v1";

    private final RegistryScreenRepository screenRepository;

    public CreateRegistryScreenToolAdapter(RegistryScreenRepository screenRepository) {
        this.screenRepository = screenRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new screen in the registry. Use when the user asks to add or create a new screen for an application. "
                + "Required: applicationId, projectId, and name. Optional: routePath (the URL path like \"/dashboard\").";
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
                      "description": "The UUID of the application to add the screen to."
                    },
                    "projectId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the project."
                    },
                    "name": {
                      "type": "string",
                      "description": "The name of the screen."
                    },
                    "routePath": {
                      "type": "string",
                      "description": "Optional URL path for the screen (e.g. /dashboard or /settings/profile)."
                    }
                  },
                  "required": ["applicationId", "projectId", "name"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String name = getString(inputArgs, "name");
        if (name != null) hints.put("name", name);
        String routePath = getString(inputArgs, "routePath");
        if (routePath != null) hints.put("routePath", routePath);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String applicationId = getString(input, "applicationId");
        String projectId = getString(input, "projectId");
        String name = getString(input, "name");
        if (applicationId == null || projectId == null || name == null || name.isBlank()) {
            return new AiActionDryRunResult(false, List.of("Missing required field: applicationId, projectId, or name"), null, false, null);
        }
        try {
            UUID.fromString(applicationId);
            UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return new AiActionDryRunResult(false, List.of("Invalid UUID format for applicationId or projectId"), null, false, null);
        }
        String diffJson = "{\"applicationId\":\"" + applicationId + "\",\"name\":\"" + name + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String applicationId = getString(input, "applicationId");
        String projectId = getString(input, "projectId");
        String name = getString(input, "name");

        if (applicationId == null || projectId == null || name == null || name.isBlank()) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID applicationUuid;
        UUID projectUuid;
        try {
            applicationUuid = UUID.fromString(applicationId);
            projectUuid = UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
        }

        String code = generateCode(name);
        String routePath = getString(input, "routePath");

        RegistryScreen screen = RegistryScreen.create(applicationUuid, projectUuid, code, name, routePath);
        RegistryScreen saved = screenRepository.save(screen);

        log.info("[CreateScreenTool] Screen created — id={} code={} application={}", saved.id(), saved.code(), applicationUuid);

        String resultToken = saved.id() + ":v1";
        String resultSummary = "{\"screenId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
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
            if (slug.length() > 12) slug = slug.substring(0, 12);
            return "SCR-" + slug;
        }
        return "SCR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
