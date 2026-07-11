package com.company.scopery.modules.project.project.infrastructure.mapper;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.infrastructure.persistence.ProjectJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectPersistenceMapper {

    public Project toDomain(ProjectJpaEntity entity) {
        return new Project(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getOwnerUserId(),
                entity.getDefaultCurrency(),
                entity.getPlannedStartDate(),
                entity.getPlannedEndDate(),
                ProjectStatus.valueOf(entity.getStatus()),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectJpaEntity toJpaEntity(Project domain) {
        ProjectJpaEntity entity = new ProjectJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setOwnerUserId(domain.ownerUserId());
        entity.setDefaultCurrency(domain.defaultCurrency());
        entity.setPlannedStartDate(domain.plannedStartDate());
        entity.setPlannedEndDate(domain.plannedEndDate());
        entity.setStatus(domain.status().name());
        entity.setVersion(domain.version());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
