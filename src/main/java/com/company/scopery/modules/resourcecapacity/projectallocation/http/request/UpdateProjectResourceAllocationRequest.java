package com.company.scopery.modules.resourcecapacity.projectallocation.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateProjectResourceAllocationRequest(
        @NotNull BigDecimal allocationPercent,
        @NotBlank String allocationType,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        String notes
) {}
