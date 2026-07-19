package com.company.scopery.modules.profitability.costsource.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProfitCostSourceJpaRepository extends JpaRepository<ProfitCostSourceJpaEntity, UUID> {
    List<ProfitCostSourceJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ProfitCostSourceJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
