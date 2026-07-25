package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class CreateRegistryApiEndpointToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateRegistryApiEndpointToolAdapter.class);

    private static final String TOOL_CODE = "create_api_endpoint";
    private static final String TOOL_VERSION = "v1";

    private static final Set<String> ALLOWED_METHODS = Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

    private final RegistryApiEndpointRepository apiEndpointRepository;

    public CreateRegistryApiEndpointToolAdapter(RegistryApiEndpointRepository apiEndpointRepository) {
        this.apiEndpointRepository = apiEndpointRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new API endpoint in the registry. Use when the user asks to add or create a new API endpoint for an application. "
                + "Required: applicationId, projectId, method (GET/POST/PUT/PATCH/DELETE), pathPattern (e.g. \"/api/users/{id}\"), and name.";
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
                      "description": "The UUID of the application to add the endpoint to."
                    },
                    "projectId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the project."
                    },
                    "method": {
                      "type": "string",
                      "description": "HTTP method. One of: GET, POST, PUT, PATCH, DELETE."
                    },
                    "pathPattern": {
                      "type": "string",
                      "description": "The URL path pattern for the endpoint (e.g. /api/users/{id} or /api/orders)."
                    },
                    "name": {
                      "type": "string",
                      "description": "The name of the endpoint."
                    }
                  },
                  "required": ["applicationId", "projectId", "method", "pathPattern", "name"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String method = getString(inputArgs, "method");
        if (method != null) hints.put("method", method);
        String pathPattern = getString(inputArgs, "pathPattern");
        if (pathPattern != null) hints.put("pathPattern", pathPattern);
        String name = getString(inputArgs, "name");
        if (name != null) hints.put("name", name);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String applicationId = getString(input, "applicationId");
        String projectId = getString(input, "projectId");
        String method = getString(input, "method");
        String pathPattern = getString(input, "pathPattern");
        String name = getString(input, "name");

        if (applicationId == null || projectId == null || method == null || pathPattern == null || name == null
                || method.isBlank() || pathPattern.isBlank() || name.isBlank()) {
            return new AiActionDryRunResult(false, List.of("Missing required field: applicationId, projectId, method, pathPattern, or name"), null, false, null);
        }
        try {
            UUID.fromString(applicationId);
            UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return new AiActionDryRunResult(false, List.of("Invalid UUID format for applicationId or projectId"), null, false, null);
        }
        if (!ALLOWED_METHODS.contains(method.trim().toUpperCase())) {
            return new AiActionDryRunResult(false, List.of("Invalid HTTP method. Must be one of: GET, POST, PUT, PATCH, DELETE"), null, false, null);
        }
        String diffJson = "{\"applicationId\":\"" + applicationId + "\",\"method\":\"" + method.trim().toUpperCase()
                + "\",\"pathPattern\":\"" + pathPattern + "\",\"name\":\"" + name + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String applicationId = getString(input, "applicationId");
        String projectId = getString(input, "projectId");
        String method = getString(input, "method");
        String pathPattern = getString(input, "pathPattern");
        String name = getString(input, "name");

        if (applicationId == null || projectId == null || method == null || pathPattern == null || name == null
                || method.isBlank() || pathPattern.isBlank() || name.isBlank()) {
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

        String normalizedMethod = method.trim().toUpperCase();
        if (!ALLOWED_METHODS.contains(normalizedMethod)) {
            return AiActionToolResult.failed("INVALID_HTTP_METHOD", false);
        }

        RegistryApiEndpoint endpoint = RegistryApiEndpoint.create(applicationUuid, projectUuid, normalizedMethod, pathPattern, name);
        RegistryApiEndpoint saved = apiEndpointRepository.save(endpoint);

        log.info("[CreateApiEndpointTool] API endpoint created — id={} method={} path={} application={}", saved.id(), saved.method(), saved.pathPattern(), applicationUuid);

        String resultToken = saved.id() + ":v1";
        String resultSummary = "{\"endpointId\":\"" + saved.id() + "\",\"method\":\"" + saved.method()
                + "\",\"pathPattern\":\"" + saved.pathPattern() + "\",\"name\":\"" + saved.name() + "\"}";

        return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
