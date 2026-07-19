package com.company.scopery.modules.resourcecapacity.risk.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataResourceRiskFlagJpaRepository extends JpaRepository<ResourceRiskFlagJpaEntity, UUID> {
    List<ResourceRiskFlagJpaEntity> findByProjectId(UUID projectId);
}
