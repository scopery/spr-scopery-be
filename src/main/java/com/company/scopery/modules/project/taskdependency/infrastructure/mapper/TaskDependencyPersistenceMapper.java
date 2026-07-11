package com.company.scopery.modules.project.taskdependency.infrastructure.mapper;

import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyStatus;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.infrastructure.persistence.TaskDependencyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDependencyPersistenceMapper {

    public TaskDependency toDomain(TaskDependencyJpaEntity entity) {
        return new TaskDependency(
                entity.getId(),
                entity.getProjectId(),
                entity.getPredecessorTaskId(),
                entity.getSuccessorTaskId(),
                TaskDependencyType.valueOf(entity.getDependencyType()),
                entity.getLagDays(),
                TaskDependencyStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TaskDependencyJpaEntity toJpaEntity(TaskDependency domain) {
        TaskDependencyJpaEntity entity = new TaskDependencyJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setPredecessorTaskId(domain.predecessorTaskId());
        entity.setSuccessorTaskId(domain.successorTaskId());
        entity.setDependencyType(domain.dependencyType().name());
        entity.setLagDays(domain.lagDays());
        entity.setStatus(domain.status().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
