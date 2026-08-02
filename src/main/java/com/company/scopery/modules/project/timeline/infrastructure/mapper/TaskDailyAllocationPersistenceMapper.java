package com.company.scopery.modules.project.timeline.infrastructure.mapper;

import com.company.scopery.modules.project.timeline.domain.enums.AllocationSource;
import com.company.scopery.modules.project.timeline.domain.model.TaskDailyAllocation;
import com.company.scopery.modules.project.timeline.infrastructure.persistence.TaskDailyAllocationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDailyAllocationPersistenceMapper {

    public TaskDailyAllocation toDomain(TaskDailyAllocationJpaEntity entity) {
        return new TaskDailyAllocation(
                entity.getId(),
                entity.getProjectId(),
                entity.getTaskId(),
                entity.getWorkDate(),
                entity.getPlannedMinutes() != null ? entity.getPlannedMinutes() : 0,
                AllocationSource.valueOf(entity.getSource()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public TaskDailyAllocationJpaEntity toJpaEntity(TaskDailyAllocation domain) {
        TaskDailyAllocationJpaEntity entity = new TaskDailyAllocationJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setTaskId(domain.taskId());
        entity.setWorkDate(domain.workDate());
        entity.setPlannedMinutes(domain.plannedMinutes());
        entity.setSource(domain.source().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
