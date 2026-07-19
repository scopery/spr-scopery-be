package com.company.scopery.modules.projectfinance.scenario.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectFinanceScenarioRepository {
    ProjectFinanceScenario save(ProjectFinanceScenario scenario);
    Optional<ProjectFinanceScenario> findById(UUID id);
    Optional<ProjectFinanceScenario> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectFinanceScenario> findByProjectId(UUID projectId);
    Optional<ProjectFinanceScenario> findCurrentByProjectId(UUID projectId);
    List<ProjectFinanceScenario> findCurrentFlagged(UUID projectId);
}
