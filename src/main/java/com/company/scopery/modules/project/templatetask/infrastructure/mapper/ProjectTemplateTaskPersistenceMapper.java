package com.company.scopery.modules.project.templatetask.infrastructure.mapper;

import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.infrastructure.persistence.ProjectTemplateTaskJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplateTaskPersistenceMapper {

    public ProjectTemplateTask toDomain(ProjectTemplateTaskJpaEntity entity) {
        return new ProjectTemplateTask(
                entity.getId(),
                entity.getTemplateVersionId(),
                entity.getTemplatePhaseId(),
                entity.getTemplateWbsNodeId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDefaultPriority() != null ? TaskPriority.valueOf(entity.getDefaultPriority()) : null,
                entity.getEstimateHours(),
                entity.getDueOffsetDays(),
                entity.getStartOffsetDays(),
                entity.getDefaultAssigneeRoleCode(),
                entity.getDeliverableDocumentTypeId(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectTemplateTaskJpaEntity toJpaEntity(ProjectTemplateTask domain) {
        ProjectTemplateTaskJpaEntity entity = new ProjectTemplateTaskJpaEntity();
        entity.setId(domain.id());
        entity.setTemplateVersionId(domain.templateVersionId());
        entity.setTemplatePhaseId(domain.templatePhaseId());
        entity.setTemplateWbsNodeId(domain.templateWbsNodeId());
        entity.setCode(domain.code());
        entity.setTitle(domain.title());
        entity.setDescription(domain.description());
        entity.setDefaultPriority(domain.defaultPriority() != null ? domain.defaultPriority().name() : null);
        entity.setEstimateHours(domain.estimateHours());
        entity.setDueOffsetDays(domain.dueOffsetDays());
        entity.setStartOffsetDays(domain.startOffsetDays());
        entity.setDefaultAssigneeRoleCode(domain.defaultAssigneeRoleCode());
        entity.setDeliverableDocumentTypeId(domain.deliverableDocumentTypeId());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
