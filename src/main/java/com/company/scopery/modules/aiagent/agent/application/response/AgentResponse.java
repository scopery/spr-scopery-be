package com.company.scopery.modules.aiagent.agent.application.response;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;

import java.time.Instant;
import java.util.UUID;

public record AgentResponse(
        UUID id,
        String name,
        String code,
        String type,
        String description,
        UUID defaultModelDeploymentId,
        String outputFormat,
        String autonomyLevel,
        String scope,
        UUID organizationId,
        UUID workspaceId,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static AgentResponse from(Agent agent) {
        return new AgentResponse(
                agent.id(),
                agent.name(),
                agent.code().value(),
                agent.type().name(),
                agent.description(),
                agent.defaultModelDeploymentId(),
                agent.outputFormat() != null ? agent.outputFormat().name() : null,
                agent.autonomyLevel() != null ? agent.autonomyLevel().name() : null,
                agent.scope() != null ? agent.scope().name() : null,
                agent.organizationId(),
                agent.workspaceId(),
                agent.status().name(),
                agent.createdAt(),
                agent.updatedAt()
        );
    }
}
