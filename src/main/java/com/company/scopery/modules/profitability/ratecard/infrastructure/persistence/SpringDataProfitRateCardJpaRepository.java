package com.company.scopery.modules.profitability.ratecard.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProfitRateCardJpaRepository extends JpaRepository<ProfitRateCardJpaEntity, UUID> {
    List<ProfitRateCardJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
    List<ProfitRateCardJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    boolean existsByWorkspaceIdAndProjectIdAndRateCode(UUID workspaceId, UUID projectId, String rateCode);
}
