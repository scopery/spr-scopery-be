package com.company.scopery.modules.profitability.riskflag.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProfitRiskFlagJpaRepository extends JpaRepository<ProfitRiskFlagJpaEntity, UUID> {
    List<ProfitRiskFlagJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<ProfitRiskFlagJpaEntity> findByProjectIdAndStatusOrderByCreatedAtDesc(UUID projectId, String status);
}
