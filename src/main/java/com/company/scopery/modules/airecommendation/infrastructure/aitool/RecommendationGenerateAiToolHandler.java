package com.company.scopery.modules.airecommendation.infrastructure.aitool;

import com.company.scopery.modules.aiagent.tool.application.port.AiToolExecutionContext;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolHandler;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResult;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResultItem;
import com.company.scopery.modules.airecommendation.application.action.CreateRecommendationRunAction;
import com.company.scopery.modules.airecommendation.application.command.CreateRecommendationRunCommand;
import com.company.scopery.modules.airecommendation.application.response.CreateRunResponse;
import com.company.scopery.modules.airecommendation.domain.enums.TriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RecommendationGenerateAiToolHandler implements AiToolHandler {

    private static final Logger log = LoggerFactory.getLogger(RecommendationGenerateAiToolHandler.class);
    private static final String TOOL_CODE = "recommendation.generate";
    private static final String TOOL_VERSION = "v1";

    private final CreateRecommendationRunAction createRunAction;

    public RecommendationGenerateAiToolHandler(CreateRecommendationRunAction createRunAction) {
        this.createRunAction = createRunAction;
    }

    @Override
    public String toolCode() {
        return TOOL_CODE;
    }

    @Override
    public String toolVersion() {
        return TOOL_VERSION;
    }

    @Override
    public boolean readOnly() {
        return true;
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments, AiToolExecutionContext context) {
        if (context.projectId() == null) {
            return AiToolResult.failure("RECOMMENDATION_PROJECT_REQUIRED", "projectId is required in execution context");
        }

        String policyCode = extractString(arguments, "policyCode");
        if (policyCode == null || policyCode.isBlank()) {
            policyCode = "PROJECT_RECOMMENDATION_MVP_V1";
        }

        @SuppressWarnings("unchecked")
        List<String> packCodes = arguments.get("packCodes") instanceof List<?> list
                ? (List<String>) list : List.of();

        String idempotencyKey = extractString(arguments, "idempotencyKey");

        CreateRecommendationRunCommand cmd = new CreateRecommendationRunCommand(
                context.workspaceId(),
                context.projectId(),
                context.actorId(),
                policyCode,
                packCodes,
                TriggerType.CHAT,
                idempotencyKey,
                null, null, null,
                context.traceId()
        );

        try {
            CreateRunResponse response = createRunAction.execute(cmd);
            String snippet = "runId=" + response.runId() + " status=" + response.status();
            AiToolResultItem item = new AiToolResultItem(
                    null, null, "recommendation_run", null, null,
                    "Recommendation run started", List.of(),
                    snippet, 1.0, 1, null
            );
            return AiToolResult.success(1, false, context.traceId(), List.of(item));
        } catch (Exception e) {
            log.warn("RecommendationGenerateAiToolHandler: run failed traceId={} error={}", context.traceId(), e.getMessage());
            return AiToolResult.failure("RECOMMENDATION_RUN_FAILED", "Failed to start recommendation run");
        }
    }

    private String extractString(Map<String, Object> args, String key) {
        Object val = args.get(key);
        return val instanceof String s ? s : null;
    }
}
