package com.company.scopery.modules.resourcecapacity.calculation.http.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CalculateCapacityRequest(
        @NotNull UUID userId,
        UUID projectId,
        @NotNull LocalDate fromDate,
        @NotNull LocalDate toDate
) {}
