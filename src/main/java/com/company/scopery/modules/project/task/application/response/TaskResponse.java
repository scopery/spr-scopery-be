package com.company.scopery.modules.project.task.application.response;

import com.company.scopery.modules.project.task.domain.model.Task;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
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
        String priority,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static TaskResponse from(Task t) {
        return new TaskResponse(
                t.id(),
                t.projectId(),
                t.projectPhaseId(),
                t.wbsNodeId(),
                t.code(),
                t.title(),
                t.description(),
                t.inChargeUserId(),
                t.plannedRoleCode(),
                t.plannedRoleName(),
                t.estimateHours(),
                t.plannedStartDate(),
                t.dueDate(),
                t.priority().name(),
                t.status().name(),
                t.version(),
                t.createdAt(),
                t.updatedAt()
        );
    }
}
