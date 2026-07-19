package com.company.scopery.modules.estimation.estimationrun.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstimationRunRepository {
    EstimationRun save(EstimationRun run);
    Optional<EstimationRun> findById(UUID id);
    List<EstimationRun> findAllByProjectId(UUID projectId);
    Optional<EstimationRun> findLatestCompletedByProjectId(UUID projectId);
    Optional<EstimationRun> findCurrent(UUID projectId, UUID currentEstimationRunId);
}
