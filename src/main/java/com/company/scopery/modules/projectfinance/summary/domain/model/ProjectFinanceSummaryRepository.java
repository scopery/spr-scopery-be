package com.company.scopery.modules.projectfinance.summary.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface ProjectFinanceSummaryRepository {
    ProjectFinanceSummary save(ProjectFinanceSummary summary);
    Optional<ProjectFinanceSummary> findByScenarioId(UUID scenarioId);
    void deleteByScenarioId(UUID scenarioId);
}
