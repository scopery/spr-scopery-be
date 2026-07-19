package com.company.scopery.modules.project.templatewbs.infrastructure.mapper;

import com.company.scopery.modules.project.templatewbs.domain.enums.TemplateWbsNodeType;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.infrastructure.persistence.ProjectTemplateWbsNodeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplateWbsNodePersistenceMapper {

    public ProjectTemplateWbsNode toDomain(ProjectTemplateWbsNodeJpaEntity entity) {
        return new ProjectTemplateWbsNode(
                entity.getId(),
                entity.getTemplateVersionId(),
                entity.getParentId(),
                entity.getTemplatePhaseId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getNodeType() != null ? TemplateWbsNodeType.valueOf(entity.getNodeType()) : null,
                entity.getDepth(),
                entity.getOrderIndex(),
                entity.getDeliverableDocumentTypeId(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectTemplateWbsNodeJpaEntity toJpaEntity(ProjectTemplateWbsNode domain) {
        ProjectTemplateWbsNodeJpaEntity entity = new ProjectTemplateWbsNodeJpaEntity();
        entity.setId(domain.id());
        entity.setTemplateVersionId(domain.templateVersionId());
        entity.setParentId(domain.parentId());
        entity.setTemplatePhaseId(domain.templatePhaseId());
        entity.setCode(domain.code());
        entity.setTitle(domain.title());
        entity.setDescription(domain.description());
        entity.setNodeType(domain.nodeType() != null ? domain.nodeType().name() : null);
        entity.setDepth(domain.depth());
        entity.setOrderIndex(domain.orderIndex());
        entity.setDeliverableDocumentTypeId(domain.deliverableDocumentTypeId());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
