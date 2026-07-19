package com.company.scopery.modules.project.scheduleoverride.infrastructure.mapper;

import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideStatus;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.infrastructure.persistence.TaskScheduleOverrideJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskScheduleOverridePersistenceMapper {

    public TaskScheduleOverride toDomain(TaskScheduleOverrideJpaEntity entity) {
        return new TaskScheduleOverride(
                entity.getId(),
                entity.getProjectId(),
                entity.getTaskId(),
                ScheduleOverrideType.valueOf(entity.getOverrideType()),
                entity.getManualStartDate(),
                entity.getManualFinishDate(),
                entity.getManualDueDate(),
                entity.getReason(),
                ScheduleOverrideStatus.valueOf(entity.getStatus()),
                entity.getCancelledAt(),
                entity.getCancelledBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TaskScheduleOverrideJpaEntity toJpaEntity(TaskScheduleOverride domain) {
        TaskScheduleOverrideJpaEntity entity = new TaskScheduleOverrideJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setTaskId(domain.taskId());
        entity.setOverrideType(domain.overrideType().name());
        entity.setManualStartDate(domain.manualStartDate());
        entity.setManualFinishDate(domain.manualFinishDate());
        entity.setManualDueDate(domain.manualDueDate());
        entity.setReason(domain.reason());
        entity.setStatus(domain.status().name());
        entity.setCancelledAt(domain.cancelledAt());
        entity.setCancelledBy(domain.cancelledBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
