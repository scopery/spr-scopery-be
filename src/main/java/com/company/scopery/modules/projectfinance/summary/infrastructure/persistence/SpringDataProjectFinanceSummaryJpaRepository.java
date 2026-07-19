package com.company.scopery.modules.projectfinance.summary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectFinanceSummaryJpaRepository
        extends JpaRepository<ProjectFinanceSummaryJpaEntity, UUID> {
    Optional<ProjectFinanceSummaryJpaEntity> findByFinanceScenarioId(UUID scenarioId);
    void deleteByFinanceScenarioId(UUID scenarioId);
}
