package com.company.scopery.modules.aiagent.agent.application.response;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;

import java.time.Instant;
import java.util.UUID;

public record AgentDetailResponse(
        UUID id,
        String name,
        String code,
        String type,
        String description,
        UUID defaultModelDeploymentId,
        String defaultModelDeploymentName,
        String outputFormat,
        String autonomyLevel,
        String scope,
        UUID organizationId,
        UUID workspaceId,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static AgentDetailResponse from(Agent agent, String deploymentName) {
        return new AgentDetailResponse(
                agent.id(),
                agent.name(),
                agent.code().value(),
                agent.type().name(),
                agent.description(),
                agent.defaultModelDeploymentId(),
                deploymentName,
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
