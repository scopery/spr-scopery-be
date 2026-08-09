package com.company.scopery.modules.project.taskrolecontribution.application.response;

import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContribution;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskRoleContributionResponse(
        UUID id,
        UUID projectId,
        UUID taskId,
        UUID userId,
        String costRoleCode,
        String costRoleName,
        BigDecimal plannedHours,
        BigDecimal actualHours,
        BigDecimal rateSnapshotPerHour,
        String currencyCode,
        BigDecimal estimatedCost,
        LocalDate periodStart,
        LocalDate periodEnd,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskRoleContributionResponse from(TaskRoleContribution c) {
        return new TaskRoleContributionResponse(
                c.id(), c.projectId(), c.taskId(), c.userId(),
                c.costRoleCode(), c.costRoleName(),
                c.plannedHours(), c.actualHours(),
                c.rateSnapshotPerHour(), c.currencyCode(),
                c.estimatedCost(),
                c.periodStart(), c.periodEnd(), c.notes(),
                c.createdAt(), c.updatedAt());
    }
}
