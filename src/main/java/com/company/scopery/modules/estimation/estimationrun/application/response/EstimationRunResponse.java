package com.company.scopery.modules.estimation.estimationrun.application.response;

import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;

import java.time.Instant;
import java.util.UUID;

public record EstimationRunResponse(
        UUID id, UUID projectId, UUID workspaceId, UUID scheduleRunId,
        String name, String description, String status, String calculationMode,
        String rateTargetDateStrategy, String currencyPolicy,
        String assumptionsJson, String resultSummaryJson,
        String errorCode, String errorMessage,
        Instant startedAt, Instant completedAt, UUID actorUserId, String traceId,
        Instant createdAt, Instant updatedAt
) {
    public static EstimationRunResponse from(EstimationRun run) {
        return new EstimationRunResponse(
                run.id(), run.projectId(), run.workspaceId(), run.scheduleRunId(),
                run.name(), run.description(), run.status().name(), run.calculationMode().name(),
                run.rateTargetDateStrategy().name(), run.currencyPolicy().name(),
                run.assumptionsJson(), run.resultSummaryJson(),
                run.errorCode(), run.errorMessage(),
                run.startedAt(), run.completedAt(), run.actorUserId(), run.traceId(),
                run.createdAt(), run.updatedAt());
    }
}
