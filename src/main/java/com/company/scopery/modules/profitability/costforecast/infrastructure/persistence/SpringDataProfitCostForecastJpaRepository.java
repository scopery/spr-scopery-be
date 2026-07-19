package com.company.scopery.modules.profitability.costforecast.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProfitCostForecastJpaRepository extends JpaRepository<ProfitCostForecastJpaEntity, UUID> {
    List<ProfitCostForecastJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<ProfitCostForecastJpaEntity> findByProjectIdAndStatusOrderByCreatedAtDesc(UUID projectId, String status);
}
