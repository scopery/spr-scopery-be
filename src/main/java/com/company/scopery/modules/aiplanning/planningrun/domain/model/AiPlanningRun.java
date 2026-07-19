package com.company.scopery.modules.aiplanning.planningrun.domain.model;

import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunStatus;
import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunType;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningRun(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID actorUserId,
        UUID agentId,
        UUID agentVersionId,
        UUID promptTemplateId,
        UUID promptTemplateVersionId,
        UUID modelDeploymentId,
        UUID aiExecutionLogId,
        PlanningRunType runType,
        PlanningRunStatus status,
        String inputSummaryJson,
        UUID contextSnapshotId,
        String outputSummaryJson,
        String errorCode,
        String errorMessage,
        Instant startedAt,
        Instant completedAt,
        String traceId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiPlanningRun create(UUID projectId, UUID workspaceId, UUID actorUserId,
                                       UUID agentId, PlanningRunType runType, String inputSummaryJson, String traceId) {
        Instant now = Instant.now();
        return new AiPlanningRun(
                UUID.randomUUID(), projectId, workspaceId, actorUserId, agentId, null, null, null, null, null,
                runType, PlanningRunStatus.PENDING, inputSummaryJson, null, null, null, null,
                now, null, traceId, 0, now, now);
    }

    public AiPlanningRun markRunning() {
        return withStatus(PlanningRunStatus.RUNNING, null, null, Instant.now(), completedAt);
    }

    public AiPlanningRun markCompleted(UUID contextSnapshotId, String outputSummaryJson) {
        return new AiPlanningRun(
                id, projectId, workspaceId, actorUserId, agentId, agentVersionId, promptTemplateId,
                promptTemplateVersionId, modelDeploymentId, aiExecutionLogId, runType, PlanningRunStatus.COMPLETED,
                inputSummaryJson, contextSnapshotId, outputSummaryJson, null, null, startedAt, Instant.now(),
                traceId, version, createdAt, Instant.now());
    }

    public AiPlanningRun markFailed(String errorCode, String errorMessage) {
        return withStatus(PlanningRunStatus.FAILED, errorCode, errorMessage, startedAt, Instant.now());
    }

    public AiPlanningRun cancel() {
        if (status == PlanningRunStatus.COMPLETED || status == PlanningRunStatus.FAILED || status == PlanningRunStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel run in status " + status);
        }
        return withStatus(PlanningRunStatus.CANCELLED, null, null, startedAt, Instant.now());
    }

    private AiPlanningRun withStatus(PlanningRunStatus status, String errorCode, String errorMessage,
                                     Instant startedAt, Instant completedAt) {
        return new AiPlanningRun(
                id, projectId, workspaceId, actorUserId, agentId, agentVersionId, promptTemplateId,
                promptTemplateVersionId, modelDeploymentId, aiExecutionLogId, runType, status,
                inputSummaryJson, contextSnapshotId, outputSummaryJson, errorCode, errorMessage,
                startedAt, completedAt, traceId, version, createdAt, Instant.now());
    }
}
