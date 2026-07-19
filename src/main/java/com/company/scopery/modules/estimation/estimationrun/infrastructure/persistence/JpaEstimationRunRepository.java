package com.company.scopery.modules.estimation.estimationrun.infrastructure.persistence;

import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.estimationrun.infrastructure.mapper.EstimationRunPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEstimationRunRepository implements EstimationRunRepository {

    private final SpringDataEstimationRunJpaRepository springData;
    private final EstimationRunPersistenceMapper mapper;

    public JpaEstimationRunRepository(SpringDataEstimationRunJpaRepository springData,
                                      EstimationRunPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public EstimationRun save(EstimationRun run) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(run)));
    }

    @Override
    public Optional<EstimationRun> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<EstimationRun> findAllByProjectId(UUID projectId) {
        return springData.findAllByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<EstimationRun> findLatestCompletedByProjectId(UUID projectId) {
        return springData.findCompletedByProjectId(projectId).stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<EstimationRun> findCurrent(UUID projectId, UUID currentEstimationRunId) {
        if (currentEstimationRunId == null) {
            return Optional.empty();
        }
        return findById(currentEstimationRunId).filter(r -> r.projectId().equals(projectId));
    }
}
