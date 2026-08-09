package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataCustomCostJpaRepository extends JpaRepository<CustomCostJpaEntity, UUID> {

    List<CustomCostJpaEntity> findAllByFinanceScenarioId(UUID financeScenarioId);

    @Query("SELECT e FROM CustomCostJpaEntity e WHERE e.financeScenarioId = :scenarioId AND e.status = 'ACTIVE'")
    List<CustomCostJpaEntity> findActiveByFinanceScenarioId(@Param("scenarioId") UUID scenarioId);
}
