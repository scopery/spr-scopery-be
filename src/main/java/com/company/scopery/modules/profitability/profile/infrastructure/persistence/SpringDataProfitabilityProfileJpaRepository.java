package com.company.scopery.modules.profitability.profile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataProfitabilityProfileJpaRepository extends JpaRepository<ProfitabilityProfileJpaEntity, UUID> {
    Optional<ProfitabilityProfileJpaEntity> findByProjectId(UUID projectId);
    boolean existsByProjectId(UUID projectId);
}
