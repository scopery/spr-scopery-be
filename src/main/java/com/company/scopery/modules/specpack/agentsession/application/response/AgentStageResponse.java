package com.company.scopery.modules.specpack.agentsession.application.response;

import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentStage;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AgentStageResponse(
        UUID id,
        UUID sessionId,
        String stageCode,
        String stageStatus,
        Map<String, Object> resultJson,
        Instant completedAt,
        Instant createdAt
) {
    public static AgentStageResponse from(SpecPackAgentStage stage) {
        return new AgentStageResponse(
                stage.id(),
                stage.sessionId(),
                stage.stageCode().name(),
                stage.stageStatus().name(),
                stage.resultJson(),
                stage.completedAt(),
                stage.createdAt()
        );
    }
}
