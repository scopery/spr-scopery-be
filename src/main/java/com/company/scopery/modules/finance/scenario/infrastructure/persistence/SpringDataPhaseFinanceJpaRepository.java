package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPhaseFinanceJpaRepository extends JpaRepository<PhaseFinanceJpaEntity, UUID> {

    List<PhaseFinanceJpaEntity> findAllByFinanceScenarioIdOrderByPhaseOrderAsc(UUID financeScenarioId);

    Optional<PhaseFinanceJpaEntity> findByFinanceScenarioIdAndProjectPhaseId(UUID financeScenarioId, UUID projectPhaseId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM PhaseFinanceJpaEntity e WHERE e.financeScenarioId = :scenarioId")
    void deleteAllByFinanceScenarioId(@Param("scenarioId") UUID scenarioId);
}
