package com.company.scopery.modules.clientportal.policy.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ExternalPortalPermissionPolicyRepository {
    ExternalPortalPermissionPolicy save(ExternalPortalPermissionPolicy entity);
    Optional<ExternalPortalPermissionPolicy> findById(UUID id);
    Optional<ExternalPortalPermissionPolicy> findByWorkspaceIdAndCode(UUID workspaceId, String code);
    List<ExternalPortalPermissionPolicy> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code);
}
