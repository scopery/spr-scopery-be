package com.company.scopery.modules.profitability.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProfitabilityPlanJpaRepository extends JpaRepository<ProfitabilityPlanJpaEntity, UUID> {
    List<ProfitabilityPlanJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ProfitabilityPlanJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
