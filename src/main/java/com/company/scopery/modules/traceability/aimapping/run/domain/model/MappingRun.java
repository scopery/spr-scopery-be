package com.company.scopery.modules.traceability.aimapping.run.domain.model;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRunStatus;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingScope;

import java.time.Instant;
import java.util.UUID;

public record MappingRun(
        UUID id,
        UUID projectId,
        MappingRelationType relationType,
        MappingScope scope,
        UUID modelDeploymentId,
        String promptKey,
        int promptVersion,
        int summaryVersion,
        int candidateLimit,
        UUID requestedBy,
        Instant startedAt,
        Instant completedAt,
        MappingRunStatus status,
        Integer sourceCount,
        int processedSourceCount,
        Integer suggestionCount,
        String tokenUsageJson,
        Instant createdAt,
        Instant updatedAt
) {
    public static MappingRun create(UUID projectId, MappingRelationType relationType,
                                    MappingScope scope, UUID modelDeploymentId,
                                    String promptKey, int candidateLimit, UUID requestedBy) {
        Instant now = Instant.now();
        return new MappingRun(
                UUID.randomUUID(), projectId, relationType, scope,
                modelDeploymentId, promptKey, 1, 1, candidateLimit,
                requestedBy, null, null,
                MappingRunStatus.PENDING,
                null, 0, null, null,
                now, now);
    }

    public MappingRun withProgress(MappingRunStatus status,
                                   Integer sourceCount,
                                   int processedSourceCount,
                                   Integer suggestionCount,
                                   String tokenUsageJson,
                                   Instant startedAt,
                                   Instant completedAt) {
        return new MappingRun(
                id, projectId, relationType, scope,
                modelDeploymentId, promptKey, promptVersion, summaryVersion,
                candidateLimit, requestedBy,
                startedAt, completedAt, status,
                sourceCount, processedSourceCount, suggestionCount, tokenUsageJson,
                createdAt, Instant.now()
        );
    }
}
