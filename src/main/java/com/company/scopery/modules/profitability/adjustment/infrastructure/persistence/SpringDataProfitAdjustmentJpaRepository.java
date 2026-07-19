package com.company.scopery.modules.profitability.adjustment.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataProfitAdjustmentJpaRepository extends JpaRepository<ProfitAdjustmentJpaEntity, UUID> {
    List<ProfitAdjustmentJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ProfitAdjustmentJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
