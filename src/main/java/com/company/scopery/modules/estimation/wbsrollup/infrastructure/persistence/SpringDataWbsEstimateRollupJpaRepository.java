package com.company.scopery.modules.estimation.wbsrollup.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataWbsEstimateRollupJpaRepository extends JpaRepository<WbsEstimateRollupJpaEntity, UUID> {
    List<WbsEstimateRollupJpaEntity> findAllByEstimationRunIdOrderByDepthAsc(UUID estimationRunId);
}
