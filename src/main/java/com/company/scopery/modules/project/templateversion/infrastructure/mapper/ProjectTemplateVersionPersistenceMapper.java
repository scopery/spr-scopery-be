package com.company.scopery.modules.project.templateversion.infrastructure.mapper;

import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.infrastructure.persistence.ProjectTemplateVersionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplateVersionPersistenceMapper {

    public ProjectTemplateVersion toDomain(ProjectTemplateVersionJpaEntity entity) {
        return new ProjectTemplateVersion(
                entity.getId(),
                entity.getProjectTemplateId(),
                entity.getVersionNumber(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus() != null ? ProjectTemplateVersionStatus.valueOf(entity.getStatus()) : null,
                entity.getPublishedAt(),
                entity.getPublishedBy(),
                entity.getArchivedAt(),
                entity.getArchivedBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectTemplateVersionJpaEntity toJpaEntity(ProjectTemplateVersion domain) {
        ProjectTemplateVersionJpaEntity entity = new ProjectTemplateVersionJpaEntity();
        entity.setId(domain.id());
        entity.setProjectTemplateId(domain.projectTemplateId());
        entity.setVersionNumber(domain.versionNumber());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setStatus(domain.status() != null ? domain.status().name() : null);
        entity.setPublishedAt(domain.publishedAt());
        entity.setPublishedBy(domain.publishedBy());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
