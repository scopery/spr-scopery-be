package com.company.scopery.modules.estimation.tasksnapshot.domain.model;

import com.company.scopery.modules.estimation.tasksnapshot.domain.enums.TaskSnapshotStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskEstimateSnapshot(
        UUID id,
        UUID estimationRunId,
        UUID projectId,
        UUID projectPhaseId,
        UUID wbsNodeId,
        UUID taskId,
        String taskCode,
        String taskTitle,
        UUID assigneeUserId,
        UUID workspaceMemberId,
        UUID costRoleId,
        String costRoleCode,
        BigDecimal estimateHours,
        LocalDate rateTargetDate,
        UUID rateCardId,
        UUID rateCardVersionId,
        UUID rateCardLineId,
        BigDecimal baseCostRate,
        BigDecimal adjustedCostRate,
        BigDecimal baseBillingRate,
        BigDecimal adjustedBillingRate,
        String currencyCode,
        UUID inflationPolicyId,
        BigDecimal inflationPercent,
        BigDecimal yearsForward,
        Instant resolvedAt,
        BigDecimal estimatedLaborCost,
        BigDecimal estimatedBillingPreview,
        TaskSnapshotStatus status,
        String issueCode,
        String issueMessage,
        Instant createdAt
) {
    public static TaskEstimateSnapshot create(
            UUID estimationRunId, UUID projectId, UUID projectPhaseId, UUID wbsNodeId,
            UUID taskId, String taskCode, String taskTitle, UUID assigneeUserId,
            UUID workspaceMemberId, UUID costRoleId, String costRoleCode,
            BigDecimal estimateHours, LocalDate rateTargetDate,
            UUID rateCardId, UUID rateCardVersionId, UUID rateCardLineId,
            BigDecimal baseCostRate, BigDecimal adjustedCostRate,
            BigDecimal baseBillingRate, BigDecimal adjustedBillingRate,
            String currencyCode, UUID inflationPolicyId, BigDecimal inflationPercent,
            BigDecimal yearsForward, Instant resolvedAt,
            BigDecimal estimatedLaborCost, BigDecimal estimatedBillingPreview,
            TaskSnapshotStatus status, String issueCode, String issueMessage) {
        return new TaskEstimateSnapshot(
                UUID.randomUUID(), estimationRunId, projectId, projectPhaseId, wbsNodeId,
                taskId, taskCode, taskTitle, assigneeUserId, workspaceMemberId,
                costRoleId, costRoleCode, estimateHours, rateTargetDate,
                rateCardId, rateCardVersionId, rateCardLineId,
                baseCostRate, adjustedCostRate, baseBillingRate, adjustedBillingRate,
                currencyCode, inflationPolicyId, inflationPercent, yearsForward, resolvedAt,
                estimatedLaborCost, estimatedBillingPreview, status, issueCode, issueMessage, null);
    }
}
