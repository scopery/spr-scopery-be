package com.company.scopery.modules.project.task.domain.model;

import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Task planning aggregate. Status {@link TaskStatus#DONE} maps to TO-BE COMPLETED
 * in later phases; Phase 09 keeps DONE for backward compatibility.
 */
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
        Instant startedAt,
        UUID startedBy,
        Instant blockedAt,
        Instant completedAt,
        UUID completedBy,
        Instant cancelledAt,
        UUID cancelledBy,
        Instant archivedAt,
        UUID archivedBy,
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
                null, null, null, null, null, null, null, null, null,
                0,
                null,
                null
        );
    }

    public Task update(
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
            TaskPriority priority) {
        return new Task(
                this.id,
                this.projectId,
                projectPhaseId,
                wbsNodeId,
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
                this.startedAt, this.startedBy, this.blockedAt,
                this.completedAt, this.completedBy,
                this.cancelledAt, this.cancelledBy,
                this.archivedAt, this.archivedBy,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public Task start(UUID actorId) {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.IN_PROGRESS,
                Instant.now(), actorId, this.blockedAt,
                this.completedAt, this.completedBy,
                this.cancelledAt, this.cancelledBy,
                this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public Task block() {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.BLOCKED,
                this.startedAt, this.startedBy, Instant.now(),
                this.completedAt, this.completedBy,
                this.cancelledAt, this.cancelledBy,
                this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public Task complete(UUID actorId) {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.DONE,
                this.startedAt, this.startedBy, this.blockedAt,
                Instant.now(), actorId,
                this.cancelledAt, this.cancelledBy,
                this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public Task cancel(UUID actorId) {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.CANCELLED,
                this.startedAt, this.startedBy, this.blockedAt,
                this.completedAt, this.completedBy,
                Instant.now(), actorId,
                this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public Task archive(UUID actorId) {
        return new Task(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.title, this.description, this.inChargeUserId,
                this.plannedRoleCode, this.plannedRoleName, this.estimateHours,
                this.plannedStartDate, this.dueDate, this.priority,
                TaskStatus.ARCHIVED,
                this.startedAt, this.startedBy, this.blockedAt,
                this.completedAt, this.completedBy,
                this.cancelledAt, this.cancelledBy,
                Instant.now(), actorId,
                this.version, this.createdAt, this.updatedAt
        );
    }
}
