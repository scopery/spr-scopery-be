package com.company.scopery.modules.clientportal.policy.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataExternalPortalPermissionPolicyJpaRepository extends JpaRepository<ExternalPortalPermissionPolicyJpaEntity, UUID> {
    Optional<ExternalPortalPermissionPolicyJpaEntity> findByWorkspaceIdAndCode(UUID workspaceId, String code);
    List<ExternalPortalPermissionPolicyJpaEntity> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code);
}
