package com.company.scopery.modules.profitability.variance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProfitVarianceJpaRepository extends JpaRepository<ProfitVarianceJpaEntity, UUID> {
    List<ProfitVarianceJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
