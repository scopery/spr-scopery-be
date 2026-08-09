package com.company.scopery.modules.profitability.summary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataProfitabilitySummaryJpaRepository extends JpaRepository<ProfitabilitySummaryJpaEntity, UUID> {
    Optional<ProfitabilitySummaryJpaEntity> findByProjectId(UUID projectId);
}
