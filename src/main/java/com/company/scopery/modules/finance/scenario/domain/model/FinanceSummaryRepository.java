package com.company.scopery.modules.finance.scenario.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface FinanceSummaryRepository {
    Optional<FinanceSummary> findByScenarioId(UUID scenarioId);
    FinanceSummary save(FinanceSummary summary);
}
