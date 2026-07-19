package com.company.scopery.modules.project.task.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateTaskCommand(
        UUID id,
        UUID projectId,
        UUID projectPhaseId,
        UUID wbsNodeId,
        String title,
        String description,
        UUID inChargeUserId,
        String plannedRoleCode,
        String plannedRoleName,
        BigDecimal estimateHours,
        LocalDate plannedStartDate,
        LocalDate dueDate,
        String priority
) {}
