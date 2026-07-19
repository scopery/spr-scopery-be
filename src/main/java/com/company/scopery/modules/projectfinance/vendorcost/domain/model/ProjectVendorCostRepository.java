package com.company.scopery.modules.projectfinance.vendorcost.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectVendorCostRepository {
    ProjectVendorCost save(ProjectVendorCost cost);
    Optional<ProjectVendorCost> findById(UUID id);
    Optional<ProjectVendorCost> findByIdAndScenarioId(UUID id, UUID scenarioId);
    List<ProjectVendorCost> findByScenarioId(UUID scenarioId);
    List<ProjectVendorCost> findActiveByScenarioId(UUID scenarioId);
}
