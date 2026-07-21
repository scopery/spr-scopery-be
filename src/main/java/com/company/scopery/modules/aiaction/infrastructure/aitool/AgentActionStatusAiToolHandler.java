package com.company.scopery.modules.aiaction.infrastructure.aitool;

import com.company.scopery.modules.aiaction.application.response.AiActionExecutionResponse;
import com.company.scopery.modules.aiaction.application.service.AiActionExecutionQueryService;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolExecutionContext;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolHandler;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResult;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResultItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AgentActionStatusAiToolHandler implements AiToolHandler {

    private final AiActionExecutionQueryService executionQueryService;

    public AgentActionStatusAiToolHandler(AiActionExecutionQueryService executionQueryService) {
        this.executionQueryService = executionQueryService;
    }

    @Override
    public String toolCode() {
        return "agent.action.status";
    }

    @Override
    public String toolVersion() {
        return "1";
    }

    @Override
    public boolean readOnly() {
        return true;
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments, AiToolExecutionContext context) {
        try {
            String executionIdStr = String.valueOf(arguments.getOrDefault("executionId", ""));
            UUID executionId = UUID.fromString(executionIdStr);

            AiActionExecutionResponse response = executionQueryService.getExecution(executionId);

            String currentStepOrdinalValue = response.currentStepOrdinal() != null
                    ? String.valueOf(response.currentStepOrdinal())
                    : "null";

            String safeSnippet = String.format(
                    "{\"executionId\":\"%s\",\"status\":\"%s\",\"executionVersion\":%d,\"currentStepOrdinal\":%s}",
                    response.executionId(),
                    response.status(),
                    response.executionVersion(),
                    currentStepOrdinalValue
            );

            AiToolResultItem item = new AiToolResultItem(
                    null, null, "ai-action-execution", null, null,
                    "AI Action Execution Status", List.of(),
                    safeSnippet, 1.0, 1, null
            );

            return AiToolResult.success(1, false, context.traceId(), List.of(item));

        } catch (Exception e) {
            return AiToolResult.failure("AGENT_ACTION_STATUS_FAILED", e.getMessage());
        }
    }
}
