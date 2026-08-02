package com.company.scopery.modules.project.timeline.infrastructure.persistence;

import com.company.scopery.modules.project.timeline.domain.model.TaskProgressSnapshot;
import com.company.scopery.modules.project.timeline.domain.model.TaskProgressSnapshotRepository;
import com.company.scopery.modules.project.timeline.infrastructure.mapper.TaskProgressSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTaskProgressSnapshotRepository implements TaskProgressSnapshotRepository {

    private final SpringDataTaskProgressSnapshotJpaRepository springDataRepository;
    private final TaskProgressSnapshotPersistenceMapper mapper;

    public JpaTaskProgressSnapshotRepository(
            SpringDataTaskProgressSnapshotJpaRepository springDataRepository,
            TaskProgressSnapshotPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public TaskProgressSnapshot save(TaskProgressSnapshot snapshot) {
        TaskProgressSnapshotJpaEntity entity = springDataRepository.findById(snapshot.id())
                .orElseGet(TaskProgressSnapshotJpaEntity::new);
        apply(entity, snapshot);
        TaskProgressSnapshotJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<TaskProgressSnapshot> findByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TaskProgressSnapshot> findByTaskId(UUID taskId) {
        return springDataRepository.findAllByTaskId(taskId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TaskProgressSnapshot> findByTaskIdAndSnapshotDate(UUID taskId, LocalDate snapshotDate) {
        return springDataRepository.findByTaskIdAndSnapshotDate(taskId, snapshotDate).map(mapper::toDomain);
    }

    private void apply(TaskProgressSnapshotJpaEntity entity, TaskProgressSnapshot domain) {
        if (entity.getId() == null) {
            entity.setId(domain.id());
            entity.setProjectId(domain.projectId());
            entity.setTaskId(domain.taskId());
            entity.setSnapshotDate(domain.snapshotDate());
        }
        entity.setProgressPercent(domain.progressPercent());
        entity.setTimeSpentMinutes(domain.timeSpentMinutes());
        entity.setNote(domain.note());
        entity.setRecordedBy(domain.recordedBy());
        entity.setRecordedAt(domain.recordedAt());
    }
}
