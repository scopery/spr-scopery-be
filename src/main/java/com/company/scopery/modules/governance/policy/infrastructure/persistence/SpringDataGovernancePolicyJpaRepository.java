package com.company.scopery.modules.governance.policy.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataGovernancePolicyJpaRepository extends JpaRepository<GovernancePolicyJpaEntity, UUID> {
    Optional<GovernancePolicyJpaEntity> findByWorkspaceIdAndObjectTypeCode(UUID workspaceId, String objectTypeCode);
    List<GovernancePolicyJpaEntity> findByWorkspaceId(UUID workspaceId);
}
