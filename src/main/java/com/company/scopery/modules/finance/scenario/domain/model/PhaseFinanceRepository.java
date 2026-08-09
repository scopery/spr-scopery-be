package com.company.scopery.modules.finance.scenario.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhaseFinanceRepository {
    List<PhaseFinance> findAllByScenarioId(UUID scenarioId);
    PhaseFinance save(PhaseFinance phase);
    List<PhaseFinance> saveAll(List<PhaseFinance> phases);
    void deleteAllByScenarioId(UUID scenarioId);
    Optional<PhaseFinance> findByScenarioIdAndProjectPhaseId(UUID scenarioId, UUID projectPhaseId);
}
