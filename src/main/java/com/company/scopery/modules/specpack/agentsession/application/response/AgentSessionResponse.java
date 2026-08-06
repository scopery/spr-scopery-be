package com.company.scopery.modules.specpack.agentsession.application.response;

import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;

import java.time.Instant;
import java.util.UUID;

public record AgentSessionResponse(
        UUID id,
        UUID projectId,
        UUID specPackId,
        UUID scopePackageId,
        String status,
        String currentStageCode,
        String readinessStatus,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
    public static AgentSessionResponse from(SpecPackAgentSession session) {
        return new AgentSessionResponse(
                session.id(),
                session.projectId(),
                session.specPackId(),
                session.scopePackageId(),
                session.status().name(),
                session.currentStageCode().name(),
                session.readinessStatus().name(),
                session.createdBy(),
                session.createdAt(),
                session.updatedAt()
        );
    }
}
