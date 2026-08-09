package com.company.scopery.modules.project.taskrolecontribution.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskRoleContribution(
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
        LocalDate periodStart,
        LocalDate periodEnd,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskRoleContribution create(
            UUID projectId, UUID taskId, UUID userId,
            String costRoleCode, String costRoleName,
            BigDecimal plannedHours, BigDecimal rateSnapshotPerHour, String currencyCode,
            LocalDate periodStart, LocalDate periodEnd, String notes) {
        Instant now = Instant.now();
        return new TaskRoleContribution(
                UUID.randomUUID(), projectId, taskId, userId,
                costRoleCode, costRoleName,
                plannedHours, BigDecimal.ZERO,
                rateSnapshotPerHour, currencyCode,
                periodStart, periodEnd, notes,
                now, now);
    }

    public boolean hasRateSnapshot() {
        return rateSnapshotPerHour != null && rateSnapshotPerHour.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal estimatedCost() {
        if (plannedHours == null || rateSnapshotPerHour == null) return BigDecimal.ZERO;
        return plannedHours.multiply(rateSnapshotPerHour);
    }
}
