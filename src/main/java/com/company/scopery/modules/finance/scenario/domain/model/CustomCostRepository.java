package com.company.scopery.modules.finance.scenario.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomCostRepository {
    List<CustomCost> findAllByScenarioId(UUID scenarioId);
    List<CustomCost> findActiveByScenarioId(UUID scenarioId);
    Optional<CustomCost> findById(UUID id);
    CustomCost save(CustomCost cost);
}
