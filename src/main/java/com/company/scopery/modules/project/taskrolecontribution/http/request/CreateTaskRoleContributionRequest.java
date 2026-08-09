package com.company.scopery.modules.project.taskrolecontribution.http.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRoleContributionRequest(
        UUID userId,
        String costRoleCode,
        String costRoleName,
        @NotNull @DecimalMin("0.01") BigDecimal plannedHours,
        @DecimalMin("0") BigDecimal rateSnapshotPerHour,
        String currencyCode,
        LocalDate periodStart,
        LocalDate periodEnd,
        String notes
) {}
