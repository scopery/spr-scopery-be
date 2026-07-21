package com.company.scopery.modules.aiaction.infrastructure.aitool;

import com.company.scopery.modules.aiaction.application.action.BuildAiActionPlanAction;
import com.company.scopery.modules.aiaction.application.action.CreateAiActionRequestAction;
import com.company.scopery.modules.aiaction.application.command.BuildAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.command.CreateAiActionRequestCommand;
import com.company.scopery.modules.aiaction.application.response.AiActionPlanResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionRequestResponse;
import com.company.scopery.modules.aiaction.request.domain.enums.AiActionOriginType;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolExecutionContext;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolHandler;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResult;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResultItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AgentActionPrepareAiToolHandler implements AiToolHandler {

    private final CreateAiActionRequestAction createRequestAction;
    private final BuildAiActionPlanAction buildPlanAction;

    public AgentActionPrepareAiToolHandler(CreateAiActionRequestAction createRequestAction,
                                           BuildAiActionPlanAction buildPlanAction) {
        this.createRequestAction = createRequestAction;
        this.buildPlanAction = buildPlanAction;
    }

    @Override
    public String toolCode() {
        return "agent.action.prepare";
    }

    @Override
    public String toolVersion() {
        return "1";
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments, AiToolExecutionContext context) {
        try {
            String workspaceIdStr = String.valueOf(arguments.getOrDefault("workspaceId", ""));
            String projectIdStr = String.valueOf(arguments.getOrDefault("projectId", ""));
            String actorIdStr = String.valueOf(arguments.getOrDefault("actorId", ""));
            String intentSummary = String.valueOf(arguments.getOrDefault("intentSummary", ""));
            String idempotencyKey = String.valueOf(arguments.getOrDefault("idempotencyKey", ""));

            UUID workspaceId = UUID.fromString(workspaceIdStr);
            UUID projectId = projectIdStr.isBlank() ? null : UUID.fromString(projectIdStr);
            UUID actorId = actorIdStr.isBlank() ? null : UUID.fromString(actorIdStr);

            CreateAiActionRequestCommand createCmd = new CreateAiActionRequestCommand(
                    workspaceId,
                    projectId,
                    actorId,
                    AiActionOriginType.DIRECT_CHAT,
                    null,  // originConversationId
                    null,  // originMessageId
                    null,  // originTurnId
                    null,  // originSuggestionRef
                    null,  // legacyPhase21SuggestionId
                    intentSummary,
                    null,  // requestedActions
                    idempotencyKey
            );

            AiActionRequestResponse requestResponse = createRequestAction.execute(createCmd);

            BuildAiActionPlanCommand buildCmd = new BuildAiActionPlanCommand(
                    requestResponse.requestId(),
                    null,   // policyCode
                    null,   // idempotencyKey
                    actorId
            );

            AiActionPlanResponse planResponse = buildPlanAction.execute(buildCmd);

            String safeSnippet = String.format(
                    "{\"requestId\":\"%s\",\"planId\":\"%s\",\"planStatus\":\"%s\",\"requiresConfirmation\":%b}",
                    requestResponse.requestId(),
                    planResponse.planId(),
                    planResponse.status(),
                    planResponse.requiresConfirmation()
            );

            AiToolResultItem item = new AiToolResultItem(
                    null, null, "ai-action-plan", null, null,
                    "AI Action Plan", List.of(),
                    safeSnippet, 1.0, 1, null
            );

            return AiToolResult.success(1, false, context.traceId(), List.of(item));

        } catch (Exception e) {
            return AiToolResult.failure("AGENT_ACTION_PREPARE_FAILED", e.getMessage());
        }
    }
}
