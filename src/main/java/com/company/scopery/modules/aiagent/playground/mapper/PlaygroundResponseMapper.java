package com.company.scopery.modules.aiagent.playground.mapper;

import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.playground.application.PlaygroundMode;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundRunResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaygroundResponseMapper {

    public PlaygroundRunResponse toPlaygroundRunResponse(ExecutionRunResponse r, PlaygroundMode mode) {
        return new PlaygroundRunResponse(
                r.executionLogId(),
                r.requestId(),
                r.status(),
                mode.name(),
                r.eventConfigId(),
                r.eventDefinitionId(),
                r.agentId(),
                r.promptVersionId(),
                r.modelDeploymentId(),
                r.providerCode(),
                r.modelCode(),
                r.modelDeploymentCode(),
                r.outputText(),
                r.inputTokenCount(),
                r.outputTokenCount(),
                r.totalTokenCount(),
                r.estimatedCost(),
                r.latencyMs(),
                r.providerRequestId(),
                r.usagePolicyDecision(),
                r.usagePolicyWarnings(),
                r.errorCode(),
                r.errorMessage());
    }
}
