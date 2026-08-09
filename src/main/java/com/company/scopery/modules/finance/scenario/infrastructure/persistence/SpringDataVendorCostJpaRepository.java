package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataVendorCostJpaRepository extends JpaRepository<VendorCostJpaEntity, UUID> {

    List<VendorCostJpaEntity> findAllByFinanceScenarioId(UUID financeScenarioId);

    @Query("SELECT e FROM VendorCostJpaEntity e WHERE e.financeScenarioId = :scenarioId AND e.status = 'ACTIVE'")
    List<VendorCostJpaEntity> findActiveByFinanceScenarioId(@Param("scenarioId") UUID scenarioId);
}
