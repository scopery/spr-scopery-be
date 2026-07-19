package com.company.scopery.modules.clientportal.grant.domain.model;
import com.company.scopery.modules.clientportal.grant.domain.enums.PortalGrantStatus;
import java.time.Instant; import java.util.UUID;
public record ExternalProjectAccessGrant(UUID id, UUID projectId, UUID workspaceId, UUID portalAccountId, PortalGrantStatus status,
                                         String permissionPolicyCode, Instant expiresAt, int version, Instant createdAt, Instant updatedAt) {
    public static ExternalProjectAccessGrant create(UUID projectId, UUID workspaceId, UUID portalAccountId, String permissionPolicyCode, Instant expiresAt) {
        Instant now = Instant.now();
        return new ExternalProjectAccessGrant(UUID.randomUUID(), projectId, workspaceId, portalAccountId, PortalGrantStatus.ACTIVE, permissionPolicyCode, expiresAt, 0, now, now);
    }
    public ExternalProjectAccessGrant revoke() {
        return new ExternalProjectAccessGrant(id, projectId, workspaceId, portalAccountId, PortalGrantStatus.REVOKED, permissionPolicyCode, expiresAt, version, createdAt, Instant.now());
    }
    public boolean isEffective() {
        return status == PortalGrantStatus.ACTIVE && (expiresAt == null || expiresAt.isAfter(Instant.now()));
    }
}
