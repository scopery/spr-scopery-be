package com.company.scopery.modules.resourcecapacity.effortestimate.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataEffortEstimateJpaRepository extends JpaRepository<EffortEstimateJpaEntity, UUID> {
    List<EffortEstimateJpaEntity> findByProjectId(UUID projectId);
    List<EffortEstimateJpaEntity> findByProjectIdAndStatus(UUID projectId, String status);
}
