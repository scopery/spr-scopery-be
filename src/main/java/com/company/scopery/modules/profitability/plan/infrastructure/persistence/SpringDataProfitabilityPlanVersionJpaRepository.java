package com.company.scopery.modules.profitability.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProfitabilityPlanVersionJpaRepository extends JpaRepository<ProfitabilityPlanVersionJpaEntity, UUID> {
    List<ProfitabilityPlanVersionJpaEntity> findByProfitabilityPlanIdOrderByVersionNumberDesc(UUID planId);
    Optional<ProfitabilityPlanVersionJpaEntity> findByIdAndProfitabilityPlanId(UUID id, UUID planId);
    int countByProfitabilityPlanId(UUID planId);
}
