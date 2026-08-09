package com.company.scopery.modules.finance.scenario.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorCostRepository {
    List<VendorCost> findAllByScenarioId(UUID scenarioId);
    List<VendorCost> findActiveByScenarioId(UUID scenarioId);
    Optional<VendorCost> findById(UUID id);
    VendorCost save(VendorCost cost);
}
