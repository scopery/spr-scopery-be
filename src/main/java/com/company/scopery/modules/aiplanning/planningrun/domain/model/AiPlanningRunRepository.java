package com.company.scopery.modules.aiplanning.planningrun.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiPlanningRunRepository {
    AiPlanningRun save(AiPlanningRun run);
    Optional<AiPlanningRun> findById(UUID id);
    Optional<AiPlanningRun> findByIdAndProjectId(UUID id, UUID projectId);
    List<AiPlanningRun> findByProjectId(UUID projectId);
}
