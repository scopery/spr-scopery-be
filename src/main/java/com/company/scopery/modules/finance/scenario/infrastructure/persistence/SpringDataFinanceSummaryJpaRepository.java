package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataFinanceSummaryJpaRepository extends JpaRepository<FinanceSummaryJpaEntity, UUID> {

    Optional<FinanceSummaryJpaEntity> findByFinanceScenarioId(UUID financeScenarioId);
}
