package com.company.scopery.modules.resourcecapacity.threshold.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface SpringDataUtilizationThresholdPolicyJpaRepository extends JpaRepository<UtilizationThresholdPolicyJpaEntity, UUID> {
    Optional<UtilizationThresholdPolicyJpaEntity> findFirstByWorkspaceIdAndProjectIdIsNull(UUID workspaceId);
    Optional<UtilizationThresholdPolicyJpaEntity> findFirstByProjectId(UUID projectId);
}
