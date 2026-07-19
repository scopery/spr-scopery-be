package com.company.scopery.modules.project.milestone.domain.model;

import com.company.scopery.modules.project.milestone.domain.enums.MilestoneStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectMilestone(
        UUID id,
        UUID projectId,
        UUID projectPhaseId,
        UUID wbsNodeId,
        String code,
        String name,
        String description,
        LocalDate milestoneDate,
        MilestoneStatus status,
        Integer sortOrder,
        Instant achievedAt,
        UUID achievedBy,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectMilestone create(
            UUID projectId,
            UUID projectPhaseId,
            UUID wbsNodeId,
            String code,
            String name,
            String description,
            LocalDate milestoneDate,
            Integer sortOrder) {
        return new ProjectMilestone(
                UUID.randomUUID(),
                projectId,
                projectPhaseId,
                wbsNodeId,
                code,
                name,
                description,
                milestoneDate,
                MilestoneStatus.PLANNED,
                sortOrder,
                null,
                null,
                null,
                null,
                0,
                null,
                null
        );
    }

    public ProjectMilestone update(
            UUID projectPhaseId,
            UUID wbsNodeId,
            String code,
            String name,
            String description,
            LocalDate milestoneDate,
            Integer sortOrder) {
        return new ProjectMilestone(
                this.id,
                this.projectId,
                projectPhaseId,
                wbsNodeId,
                code,
                name,
                description,
                milestoneDate,
                this.status,
                sortOrder,
                this.achievedAt,
                this.achievedBy,
                this.archivedAt,
                this.archivedBy,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public ProjectMilestone achieve(UUID actorId) {
        return new ProjectMilestone(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.name, this.description, this.milestoneDate,
                MilestoneStatus.ACHIEVED, this.sortOrder,
                Instant.now(), actorId,
                this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public ProjectMilestone archive(UUID actorId) {
        return new ProjectMilestone(
                this.id, this.projectId, this.projectPhaseId, this.wbsNodeId,
                this.code, this.name, this.description, this.milestoneDate,
                MilestoneStatus.ARCHIVED, this.sortOrder,
                this.achievedAt, this.achievedBy,
                Instant.now(), actorId,
                this.version, this.createdAt, this.updatedAt
        );
    }
}
