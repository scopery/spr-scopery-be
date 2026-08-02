package com.company.scopery.modules.project.timeline.infrastructure.mapper;

import com.company.scopery.modules.project.timeline.domain.model.TaskProgressSnapshot;
import com.company.scopery.modules.project.timeline.infrastructure.persistence.TaskProgressSnapshotJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskProgressSnapshotPersistenceMapper {

    public TaskProgressSnapshot toDomain(TaskProgressSnapshotJpaEntity entity) {
        return new TaskProgressSnapshot(
                entity.getId(),
                entity.getProjectId(),
                entity.getTaskId(),
                entity.getSnapshotDate(),
                entity.getProgressPercent(),
                entity.getTimeSpentMinutes(),
                entity.getNote(),
                entity.getRecordedBy(),
                entity.getRecordedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public TaskProgressSnapshotJpaEntity toJpaEntity(TaskProgressSnapshot domain) {
        TaskProgressSnapshotJpaEntity entity = new TaskProgressSnapshotJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setTaskId(domain.taskId());
        entity.setSnapshotDate(domain.snapshotDate());
        entity.setProgressPercent(domain.progressPercent());
        entity.setTimeSpentMinutes(domain.timeSpentMinutes());
        entity.setNote(domain.note());
        entity.setRecordedBy(domain.recordedBy());
        entity.setRecordedAt(domain.recordedAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
