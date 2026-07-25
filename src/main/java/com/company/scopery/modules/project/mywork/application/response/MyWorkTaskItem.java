package com.company.scopery.modules.project.mywork.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public record MyWorkTaskItem(
        UUID taskId,
        UUID projectId,
        String projectCode,
        String projectName,
        String code,
        String title,
        String status,
        String priority,
        UUID inChargeUserId,
        LocalDate plannedStartDate,
        LocalDate dueDate,
        BigDecimal estimateHours,
        UUID projectPhaseId,
        String projectPhaseName,
        UUID wbsNodeId,
        boolean isOverdue,
        Instant updatedAt
) {}
