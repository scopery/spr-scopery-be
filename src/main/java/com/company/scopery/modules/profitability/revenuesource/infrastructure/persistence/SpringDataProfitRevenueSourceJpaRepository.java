package com.company.scopery.modules.profitability.revenuesource.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProfitRevenueSourceJpaRepository extends JpaRepository<ProfitRevenueSourceJpaEntity, UUID> {
    List<ProfitRevenueSourceJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ProfitRevenueSourceJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
