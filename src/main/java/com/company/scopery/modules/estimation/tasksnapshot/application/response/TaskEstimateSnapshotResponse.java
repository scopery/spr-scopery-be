package com.company.scopery.modules.estimation.tasksnapshot.application.response;

import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshot;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskEstimateSnapshotResponse(
        UUID id, UUID estimationRunId, UUID projectId, UUID projectPhaseId, UUID wbsNodeId,
        UUID taskId, String taskCode, String taskTitle, UUID assigneeUserId,
        UUID costRoleId, String costRoleCode, BigDecimal estimateHours, LocalDate rateTargetDate,
        UUID rateCardId, UUID rateCardVersionId, UUID rateCardLineId,
        BigDecimal baseCostRate, BigDecimal adjustedCostRate,
        BigDecimal baseBillingRate, BigDecimal adjustedBillingRate,
        String currencyCode, UUID inflationPolicyId, BigDecimal inflationPercent,
        BigDecimal yearsForward, Instant resolvedAt,
        BigDecimal estimatedLaborCost, BigDecimal estimatedBillingPreview,
        String status, String issueCode, String issueMessage
) {
    public static TaskEstimateSnapshotResponse from(TaskEstimateSnapshot s, boolean includeRateDetail) {
        return new TaskEstimateSnapshotResponse(
                s.id(), s.estimationRunId(), s.projectId(), s.projectPhaseId(), s.wbsNodeId(),
                s.taskId(), s.taskCode(), s.taskTitle(), s.assigneeUserId(),
                s.costRoleId(), s.costRoleCode(), s.estimateHours(), s.rateTargetDate(),
                includeRateDetail ? s.rateCardId() : null,
                includeRateDetail ? s.rateCardVersionId() : null,
                includeRateDetail ? s.rateCardLineId() : null,
                includeRateDetail ? s.baseCostRate() : null,
                includeRateDetail ? s.adjustedCostRate() : null,
                includeRateDetail ? s.baseBillingRate() : null,
                includeRateDetail ? s.adjustedBillingRate() : null,
                s.currencyCode(),
                includeRateDetail ? s.inflationPolicyId() : null,
                includeRateDetail ? s.inflationPercent() : null,
                includeRateDetail ? s.yearsForward() : null,
                includeRateDetail ? s.resolvedAt() : null,
                s.estimatedLaborCost(), s.estimatedBillingPreview(),
                s.status().name(), s.issueCode(), s.issueMessage());
    }
}
