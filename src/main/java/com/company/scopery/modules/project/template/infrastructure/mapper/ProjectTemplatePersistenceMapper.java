package com.company.scopery.modules.project.template.infrastructure.mapper;

import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateCategory;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateVisibility;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.infrastructure.persistence.ProjectTemplateJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplatePersistenceMapper {

    public ProjectTemplate toDomain(ProjectTemplateJpaEntity entity) {
        return new ProjectTemplate(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getScope() != null ? ProjectTemplateScope.valueOf(entity.getScope()) : null,
                entity.getOrganizationId(),
                entity.getWorkspaceId(),
                entity.getCategory() != null ? ProjectTemplateCategory.valueOf(entity.getCategory()) : null,
                entity.getVisibility() != null ? ProjectTemplateVisibility.valueOf(entity.getVisibility()) : null,
                entity.getStatus() != null ? ProjectTemplateStatus.valueOf(entity.getStatus()) : null,
                entity.getCurrentVersionId(),
                entity.isBuiltIn(),
                entity.getArchivedAt(),
                entity.getArchivedBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectTemplateJpaEntity toJpaEntity(ProjectTemplate domain) {
        ProjectTemplateJpaEntity entity = new ProjectTemplateJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setScope(domain.scope() != null ? domain.scope().name() : null);
        entity.setOrganizationId(domain.organizationId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCategory(domain.category() != null ? domain.category().name() : null);
        entity.setVisibility(domain.visibility() != null ? domain.visibility().name() : null);
        entity.setStatus(domain.status() != null ? domain.status().name() : null);
        entity.setCurrentVersionId(domain.currentVersionId());
        entity.setBuiltIn(domain.builtIn());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
