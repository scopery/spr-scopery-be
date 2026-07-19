package com.company.scopery.modules.project.templatephase.infrastructure.mapper;

import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.infrastructure.persistence.ProjectTemplatePhaseJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplatePhasePersistenceMapper {

    public ProjectTemplatePhase toDomain(ProjectTemplatePhaseJpaEntity entity) {
        return new ProjectTemplatePhase(
                entity.getId(),
                entity.getTemplateVersionId(),
                entity.getPhaseDefinitionId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getDisplayOrder(),
                entity.getDefaultDurationDays(),
                entity.getStartOffsetDays(),
                entity.getDeliverableDocumentTypeId(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectTemplatePhaseJpaEntity toJpaEntity(ProjectTemplatePhase domain) {
        ProjectTemplatePhaseJpaEntity entity = new ProjectTemplatePhaseJpaEntity();
        entity.setId(domain.id());
        entity.setTemplateVersionId(domain.templateVersionId());
        entity.setPhaseDefinitionId(domain.phaseDefinitionId());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setDisplayOrder(domain.displayOrder());
        entity.setDefaultDurationDays(domain.defaultDurationDays());
        entity.setStartOffsetDays(domain.startOffsetDays());
        entity.setDeliverableDocumentTypeId(domain.deliverableDocumentTypeId());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
