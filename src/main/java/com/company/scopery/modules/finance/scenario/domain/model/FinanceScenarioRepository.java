package com.company.scopery.modules.finance.scenario.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinanceScenarioRepository {
    Optional<FinanceScenario> findById(UUID id);
    Optional<FinanceScenario> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<FinanceScenario> findCurrentByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
    List<FinanceScenario> findAllByProjectId(UUID projectId);
    FinanceScenario save(FinanceScenario scenario);
    void clearCurrentFlagForProject(UUID projectId);
}
