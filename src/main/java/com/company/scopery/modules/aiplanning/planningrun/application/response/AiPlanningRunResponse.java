package com.company.scopery.modules.aiplanning.planningrun.application.response;

import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRun;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningRunResponse(
        UUID id, UUID projectId, UUID workspaceId, UUID actorUserId, UUID agentId,
        String runType, String status, String inputSummaryJson, UUID contextSnapshotId,
        String outputSummaryJson, String errorCode, String errorMessage,
        Instant startedAt, Instant completedAt, String traceId, int version,
        Instant createdAt, Instant updatedAt
) {
    public static AiPlanningRunResponse from(AiPlanningRun r) {
        return new AiPlanningRunResponse(
                r.id(), r.projectId(), r.workspaceId(), r.actorUserId(), r.agentId(),
                r.runType().name(), r.status().name(), r.inputSummaryJson(), r.contextSnapshotId(),
                r.outputSummaryJson(), r.errorCode(), r.errorMessage(),
                r.startedAt(), r.completedAt(), r.traceId(), r.version(), r.createdAt(), r.updatedAt());
    }
}
