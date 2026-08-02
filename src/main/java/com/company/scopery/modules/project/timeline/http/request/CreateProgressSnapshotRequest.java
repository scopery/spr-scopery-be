package com.company.scopery.modules.project.timeline.http.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProgressSnapshotRequest(
        @NotNull
        @DecimalMin("0")
        @DecimalMax("100")
        BigDecimal progressPercent,
        LocalDate snapshotDate,
        Integer timeSpentMinutes,
        String note
) {}
