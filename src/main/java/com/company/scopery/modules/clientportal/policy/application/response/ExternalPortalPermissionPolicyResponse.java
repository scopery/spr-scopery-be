package com.company.scopery.modules.clientportal.policy.application.response;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicy;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalPermissionPolicyResponse(UUID id, UUID workspaceId, String code, String name,
                                                     String description, String permissionsJson, Instant createdAt) {
    public static ExternalPortalPermissionPolicyResponse from(ExternalPortalPermissionPolicy p) {
        return new ExternalPortalPermissionPolicyResponse(p.id(), p.workspaceId(), p.code(), p.name(),
                p.description(), p.permissionsJson(), p.createdAt());
    }
}
