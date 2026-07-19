package com.company.scopery.modules.estimation.tasksnapshot.infrastructure.persistence;

import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshot;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshotRepository;
import com.company.scopery.modules.estimation.tasksnapshot.infrastructure.mapper.TaskEstimateSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTaskEstimateSnapshotRepository implements TaskEstimateSnapshotRepository {
    private final SpringDataTaskEstimateSnapshotJpaRepository springData;
    private final TaskEstimateSnapshotPersistenceMapper mapper;

    public JpaTaskEstimateSnapshotRepository(SpringDataTaskEstimateSnapshotJpaRepository springData,
                                             TaskEstimateSnapshotPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public TaskEstimateSnapshot save(TaskEstimateSnapshot snapshot) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(snapshot)));
    }

    @Override
    public List<TaskEstimateSnapshot> saveAll(List<TaskEstimateSnapshot> snapshots) {
        return snapshots.stream().map(this::save).toList();
    }

    @Override
    public Optional<TaskEstimateSnapshot> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TaskEstimateSnapshot> findAllByEstimationRunId(UUID estimationRunId) {
        return springData.findAllByEstimationRunIdOrderByTaskTitleAsc(estimationRunId).stream()
                .map(mapper::toDomain).toList();
    }
}
