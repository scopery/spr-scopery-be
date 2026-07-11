package com.company.scopery.modules.project.task.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskCommand(
        UUID projectId,
        UUID projectPhaseId,
        UUID wbsNodeId,
        String code,
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
