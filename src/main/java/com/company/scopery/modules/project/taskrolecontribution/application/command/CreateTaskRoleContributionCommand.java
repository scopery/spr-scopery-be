package com.company.scopery.modules.project.taskrolecontribution.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRoleContributionCommand(
        UUID projectId,
        UUID taskId,
        UUID userId,
        String costRoleCode,
        String costRoleName,
        BigDecimal plannedHours,
        BigDecimal rateSnapshotPerHour,
        String currencyCode,
        LocalDate periodStart,
        LocalDate periodEnd,
        String notes
) {}
