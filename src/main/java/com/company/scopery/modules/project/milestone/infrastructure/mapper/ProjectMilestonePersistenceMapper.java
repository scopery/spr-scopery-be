package com.company.scopery.modules.project.milestone.infrastructure.mapper;

import com.company.scopery.modules.project.milestone.domain.enums.MilestoneStatus;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.milestone.infrastructure.persistence.ProjectMilestoneJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMilestonePersistenceMapper {

    public ProjectMilestone toDomain(ProjectMilestoneJpaEntity entity) {
        return new ProjectMilestone(
                entity.getId(),
                entity.getProjectId(),
                entity.getProjectPhaseId(),
                entity.getWbsNodeId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getMilestoneDate(),
                MilestoneStatus.valueOf(entity.getStatus()),
                entity.getSortOrder(),
                entity.getAchievedAt(),
                entity.getAchievedBy(),
                entity.getArchivedAt(),
                entity.getArchivedBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectMilestoneJpaEntity toJpaEntity(ProjectMilestone domain) {
        ProjectMilestoneJpaEntity entity = new ProjectMilestoneJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setProjectPhaseId(domain.projectPhaseId());
        entity.setWbsNodeId(domain.wbsNodeId());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setMilestoneDate(domain.milestoneDate());
        entity.setStatus(domain.status().name());
        entity.setSortOrder(domain.sortOrder());
        entity.setAchievedAt(domain.achievedAt());
        entity.setAchievedBy(domain.achievedBy());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
