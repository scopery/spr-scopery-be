package com.company.scopery.modules.projectfinance.vendorcost.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectVendorCostJpaRepository
        extends JpaRepository<ProjectVendorCostJpaEntity, UUID> {
    List<ProjectVendorCostJpaEntity> findByFinanceScenarioIdOrderByCreatedAtDesc(UUID scenarioId);
    List<ProjectVendorCostJpaEntity> findByFinanceScenarioIdAndStatus(UUID scenarioId, String status);
    Optional<ProjectVendorCostJpaEntity> findByIdAndFinanceScenarioId(UUID id, UUID scenarioId);
}
