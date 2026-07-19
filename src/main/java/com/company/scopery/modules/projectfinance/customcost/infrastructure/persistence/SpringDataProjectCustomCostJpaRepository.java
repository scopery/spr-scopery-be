package com.company.scopery.modules.projectfinance.customcost.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectCustomCostJpaRepository
        extends JpaRepository<ProjectCustomCostJpaEntity, UUID> {
    List<ProjectCustomCostJpaEntity> findByFinanceScenarioIdOrderByCreatedAtDesc(UUID scenarioId);
    List<ProjectCustomCostJpaEntity> findByFinanceScenarioIdAndStatus(UUID scenarioId, String status);
    Optional<ProjectCustomCostJpaEntity> findByIdAndFinanceScenarioId(UUID id, UUID scenarioId);
}
