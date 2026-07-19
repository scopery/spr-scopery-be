package com.company.scopery.modules.project.projectphase.infrastructure.mapper;

import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.infrastructure.persistence.ProjectPhaseJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectPhasePersistenceMapper {

    public ProjectPhase toDomain(ProjectPhaseJpaEntity entity) {
        return new ProjectPhase(
                entity.getId(),
                entity.getProjectId(),
                entity.getPhaseDefinitionId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getDisplayOrder() != null ? entity.getDisplayOrder() : 0,
                entity.getPlannedStartDate(),
                entity.getPlannedEndDate(),
                ProjectPhaseStatus.valueOf(entity.getStatus()),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getArchivedAt(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectPhaseJpaEntity toJpaEntity(ProjectPhase domain) {
        ProjectPhaseJpaEntity entity = new ProjectPhaseJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setPhaseDefinitionId(domain.phaseDefinitionId());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setDisplayOrder(domain.displayOrder());
        entity.setPlannedStartDate(domain.plannedStartDate());
        entity.setPlannedEndDate(domain.plannedEndDate());
        entity.setStatus(domain.status().name());
        entity.setStartedAt(domain.startedAt());
        entity.setCompletedAt(domain.completedAt());
        entity.setArchivedAt(domain.archivedAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
