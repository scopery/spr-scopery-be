package com.company.scopery.modules.projectfinance.phasefinance.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectPhaseFinanceRepository {
    ProjectPhaseFinance save(ProjectPhaseFinance phaseFinance);
    List<ProjectPhaseFinance> saveAll(List<ProjectPhaseFinance> rows);
    Optional<ProjectPhaseFinance> findById(UUID id);
    Optional<ProjectPhaseFinance> findByIdAndScenarioId(UUID id, UUID scenarioId);
    List<ProjectPhaseFinance> findByScenarioId(UUID scenarioId);
    void deleteByScenarioId(UUID scenarioId);
}
