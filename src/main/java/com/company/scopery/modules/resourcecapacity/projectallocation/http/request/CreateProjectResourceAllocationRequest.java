package com.company.scopery.modules.resourcecapacity.projectallocation.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectResourceAllocationRequest(
        @NotNull UUID projectId,
        @NotNull UUID workspaceMemberId,
        @NotNull BigDecimal allocationPercent,
        @NotBlank String allocationType,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        String notes
) {}
