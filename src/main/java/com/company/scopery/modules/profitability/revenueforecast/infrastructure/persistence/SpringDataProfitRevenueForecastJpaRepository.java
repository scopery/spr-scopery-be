package com.company.scopery.modules.profitability.revenueforecast.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProfitRevenueForecastJpaRepository extends JpaRepository<ProfitRevenueForecastJpaEntity, UUID> {
    List<ProfitRevenueForecastJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<ProfitRevenueForecastJpaEntity> findByProjectIdAndStatusOrderByCreatedAtDesc(UUID projectId, String status);
}
