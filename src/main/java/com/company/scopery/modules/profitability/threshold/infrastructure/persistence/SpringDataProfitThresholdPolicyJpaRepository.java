package com.company.scopery.modules.profitability.threshold.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataProfitThresholdPolicyJpaRepository extends JpaRepository<ProfitThresholdPolicyJpaEntity, UUID> {
    Optional<ProfitThresholdPolicyJpaEntity> findByProjectId(UUID projectId);
}
