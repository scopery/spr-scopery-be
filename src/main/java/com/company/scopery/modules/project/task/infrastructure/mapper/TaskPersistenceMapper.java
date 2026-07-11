package com.company.scopery.modules.project.task.infrastructure.mapper;

import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.infrastructure.persistence.TaskJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskPersistenceMapper {

    public Task toDomain(TaskJpaEntity entity) {
        return new Task(
                entity.getId(),
                entity.getProjectId(),
                entity.getProjectPhaseId(),
                entity.getWbsNodeId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getInChargeUserId(),
                entity.getPlannedRoleCode(),
                entity.getPlannedRoleName(),
                entity.getEstimateHours(),
                entity.getPlannedStartDate(),
                entity.getDueDate(),
                TaskPriority.valueOf(entity.getPriority()),
                TaskStatus.valueOf(entity.getStatus()),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TaskJpaEntity toJpaEntity(Task domain) {
        TaskJpaEntity entity = new TaskJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setProjectPhaseId(domain.projectPhaseId());
        entity.setWbsNodeId(domain.wbsNodeId());
        entity.setCode(domain.code());
        entity.setTitle(domain.title());
        entity.setDescription(domain.description());
        entity.setInChargeUserId(domain.inChargeUserId());
        entity.setPlannedRoleCode(domain.plannedRoleCode());
        entity.setPlannedRoleName(domain.plannedRoleName());
        entity.setEstimateHours(domain.estimateHours());
        entity.setPlannedStartDate(domain.plannedStartDate());
        entity.setDueDate(domain.dueDate());
        entity.setPriority(domain.priority().name());
        entity.setStatus(domain.status().name());
        entity.setVersion(domain.version());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
