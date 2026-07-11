package com.company.scopery.modules.project.task.domain.model;

import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Task(
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
        TaskPriority priority,
        TaskStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static Task create(
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
            TaskPriority priority) {
        return new Task(
                UUID.randomUUID(),
                projectId,
                projectPhaseId,
                wbsNodeId,
                code,
                title,
                description,
                inChargeUserId,
                plannedRoleCode,
                plannedRoleName,
                estimateHours,
                plannedStartDate,
                dueDate,
                priority,
                TaskStatus.TODO,
                0,
                null,
                null
        );
    }

    public Task update(
            String title,
            String description,
            UUID inChargeUserId,
            String plannedRoleCode,
            String plannedRoleName,
            BigDecimal estimateHours,
            LocalDate plannedStartDate,
            LocalDate dueDate,
            TaskPriority priority) {
        return new Task(
                this.id,
                this.projectId,
                this.projectPhaseId,
                this.wbsNodeId,
                this.code,
                title,
                description,
                inChargeUserId,
                plannedRoleCode,
                plannedRoleName,
                estimateHours,
                plannedStartDate,
                dueDate,
                priority,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public Task start() {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.IN_PROGRESS, this.version, this.createdAt, this.updatedAt
        );
    }

    public Task block() {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.BLOCKED, this.version, this.createdAt, this.updatedAt
        );
    }

    public Task complete() {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.DONE, this.version, this.createdAt, this.updatedAt
        );
    }

    public Task cancel() {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.CANCELLED, this.version, this.createdAt, this.updatedAt
        );
    }

    public Task archive() {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.ARCHIVED, this.version, this.createdAt, this.updatedAt
        );
    }
}
