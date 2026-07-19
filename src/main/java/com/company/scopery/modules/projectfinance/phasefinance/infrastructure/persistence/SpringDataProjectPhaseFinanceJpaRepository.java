package com.company.scopery.modules.projectfinance.phasefinance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectPhaseFinanceJpaRepository
        extends JpaRepository<ProjectPhaseFinanceJpaEntity, UUID> {
    List<ProjectPhaseFinanceJpaEntity> findByFinanceScenarioIdOrderByPhaseOrderAsc(UUID scenarioId);
    Optional<ProjectPhaseFinanceJpaEntity> findByIdAndFinanceScenarioId(UUID id, UUID scenarioId);
    void deleteByFinanceScenarioId(UUID scenarioId);
}
