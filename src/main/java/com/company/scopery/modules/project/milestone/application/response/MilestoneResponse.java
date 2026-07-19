package com.company.scopery.modules.project.milestone.application.response;

import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MilestoneResponse(
        UUID id,
        UUID projectId,
        UUID projectPhaseId,
        UUID wbsNodeId,
        String code,
        String name,
        String description,
        LocalDate milestoneDate,
        String status,
        Integer sortOrder,
        Instant achievedAt,
        UUID achievedBy,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static MilestoneResponse from(ProjectMilestone m) {
        return new MilestoneResponse(
                m.id(), m.projectId(), m.projectPhaseId(), m.wbsNodeId(),
                m.code(), m.name(), m.description(), m.milestoneDate(),
                m.status().name(), m.sortOrder(),
                m.achievedAt(), m.achievedBy(), m.archivedAt(), m.archivedBy(),
                m.version(), m.createdAt(), m.updatedAt());
    }
}
