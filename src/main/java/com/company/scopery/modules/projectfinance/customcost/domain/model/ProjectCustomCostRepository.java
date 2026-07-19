package com.company.scopery.modules.projectfinance.customcost.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectCustomCostRepository {
    ProjectCustomCost save(ProjectCustomCost cost);
    Optional<ProjectCustomCost> findById(UUID id);
    Optional<ProjectCustomCost> findByIdAndScenarioId(UUID id, UUID scenarioId);
    List<ProjectCustomCost> findByScenarioId(UUID scenarioId);
    List<ProjectCustomCost> findActiveByScenarioId(UUID scenarioId);
}
