package com.company.scopery.modules.estimation.estimationrun.domain.model;

import com.company.scopery.modules.estimation.estimationrun.domain.enums.CalculationMode;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CurrencyPolicy;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.RateTargetDateStrategy;

import java.time.Instant;
import java.util.UUID;

public record EstimationRun(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID scheduleRunId,
        String name,
        String description,
        EstimationRunStatus status,
        CalculationMode calculationMode,
        RateTargetDateStrategy rateTargetDateStrategy,
        CurrencyPolicy currencyPolicy,
        String assumptionsJson,
        String resultSummaryJson,
        String errorCode,
        String errorMessage,
        Instant startedAt,
        Instant completedAt,
        UUID actorUserId,
        String traceId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static EstimationRun create(
            UUID projectId,
            UUID workspaceId,
            UUID scheduleRunId,
            String name,
            String description,
            CalculationMode calculationMode,
            RateTargetDateStrategy rateTargetDateStrategy,
            CurrencyPolicy currencyPolicy,
            String assumptionsJson,
            UUID actorUserId,
            String traceId) {
        return new EstimationRun(
                UUID.randomUUID(), projectId, workspaceId, scheduleRunId, name, description,
                EstimationRunStatus.PENDING, calculationMode, rateTargetDateStrategy, currencyPolicy,
                assumptionsJson, null, null, null, null, null, actorUserId, traceId, 0, null, null);
    }

    public EstimationRun running() {
        return copy(EstimationRunStatus.RUNNING, resultSummaryJson, null, null, Instant.now(), null);
    }

    public EstimationRun completed(String summary) {
        return copy(EstimationRunStatus.COMPLETED, summary, null, null, startedAt, Instant.now());
    }

    public EstimationRun failed(String code, String message) {
        return copy(EstimationRunStatus.FAILED, resultSummaryJson, code, message, startedAt, Instant.now());
    }

    public EstimationRun cancelled() {
        return copy(EstimationRunStatus.CANCELLED, resultSummaryJson, null, null, startedAt, Instant.now());
    }

    public boolean cancellable() {
        return status == EstimationRunStatus.PENDING || status == EstimationRunStatus.RUNNING;
    }

    private EstimationRun copy(EstimationRunStatus newStatus, String result, String code, String message,
                               Instant started, Instant completed) {
        return new EstimationRun(id, projectId, workspaceId, scheduleRunId, name, description, newStatus,
                calculationMode, rateTargetDateStrategy, currencyPolicy, assumptionsJson, result,
                code, message, started, completed, actorUserId, traceId, version, createdAt, updatedAt);
    }
}
