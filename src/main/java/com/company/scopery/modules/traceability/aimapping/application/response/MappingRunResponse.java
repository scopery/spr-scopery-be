package com.company.scopery.modules.traceability.aimapping.application.response;

import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRun;

import java.time.Instant;
import java.util.UUID;

public record MappingRunResponse(
        UUID id,
        UUID projectId,
        String relationType,
        String scope,
        String status,
        Integer sourceCount,
        Integer suggestionCount,
        String promptKey,
        Integer promptVersion,
        Integer summaryVersion,
        Integer candidateLimit,
        UUID modelDeploymentId,
        String tokenUsageJson,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt
) {
    public static MappingRunResponse from(MappingRun run) {
        return new MappingRunResponse(
                run.id(),
                run.projectId(),
                run.relationType().name(),
                run.scope().name(),
                run.status().name(),
                run.sourceCount(),
                run.suggestionCount(),
                run.promptKey(),
                run.promptVersion(),
                run.summaryVersion(),
                run.candidateLimit(),
                run.modelDeploymentId(),
                run.tokenUsageJson(),
                run.startedAt(),
                run.completedAt(),
                run.createdAt()
        );
    }
}
