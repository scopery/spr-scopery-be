package com.company.scopery.modules.project.templatedependency.infrastructure.mapper;

import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.infrastructure.persistence.ProjectTemplateTaskDependencyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplateTaskDependencyPersistenceMapper {

    public ProjectTemplateTaskDependency toDomain(ProjectTemplateTaskDependencyJpaEntity entity) {
        return new ProjectTemplateTaskDependency(
                entity.getId(),
                entity.getTemplateVersionId(),
                entity.getPredecessorTemplateTaskId(),
                entity.getSuccessorTemplateTaskId(),
                entity.getDependencyType() != null
                        ? TaskDependencyType.valueOf(entity.getDependencyType()) : null,
                entity.getLagDays(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectTemplateTaskDependencyJpaEntity toJpaEntity(ProjectTemplateTaskDependency domain) {
        ProjectTemplateTaskDependencyJpaEntity entity = new ProjectTemplateTaskDependencyJpaEntity();
        entity.setId(domain.id());
        entity.setTemplateVersionId(domain.templateVersionId());
        entity.setPredecessorTemplateTaskId(domain.predecessorTemplateTaskId());
        entity.setSuccessorTemplateTaskId(domain.successorTemplateTaskId());
        entity.setDependencyType(domain.dependencyType() != null ? domain.dependencyType().name() : null);
        entity.setLagDays(domain.lagDays());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
