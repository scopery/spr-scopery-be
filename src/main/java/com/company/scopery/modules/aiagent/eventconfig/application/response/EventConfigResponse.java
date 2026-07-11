package com.company.scopery.modules.aiagent.eventconfig.application.response;

import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;

import java.time.Instant;
import java.util.UUID;

public record EventConfigResponse(
        UUID id,
        String code,
        String name,
        UUID eventDefinitionId,
        String environment,
        String triggerType,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static EventConfigResponse from(EventConfig c) {
        return new EventConfigResponse(
                c.id(),
                c.code().value(),
                c.name(),
                c.eventDefinitionId(),
                c.environment().name(),
                c.triggerType().name(),
                c.agentId(),
                c.promptVersionId(),
                c.modelDeploymentId(),
                c.description(),
                c.status().name(),
                c.createdAt(),
                c.updatedAt()
        );
    }
}