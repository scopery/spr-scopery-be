package com.company.scopery.modules.project.task.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotNull UUID projectPhaseId,
        UUID wbsNodeId,
        @NotBlank String code,
        @NotBlank String title,
        String description,
        UUID inChargeUserId,
        String plannedRoleCode,
        String plannedRoleName,
        @NotNull @DecimalMin("0.01") BigDecimal estimateHours,
        LocalDate plannedStartDate,
        LocalDate dueDate,
        @Schema(allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "MEDIUM") String priority
) {}
