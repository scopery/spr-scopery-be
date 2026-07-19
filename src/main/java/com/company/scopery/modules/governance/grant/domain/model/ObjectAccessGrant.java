package com.company.scopery.modules.governance.grant.domain.model;
import java.time.Instant; import java.util.UUID;
public record ObjectAccessGrant(UUID id, UUID workspaceId, UUID projectId, String objectTypeCode, UUID targetId,
        String granteeType, UUID granteeId, String grantRole, String status, Instant expiresAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static ObjectAccessGrant create(UUID workspaceId, UUID projectId, String objectType, UUID targetId,
                                            String granteeType, UUID granteeId, String grantRole) {
        Instant now = Instant.now();
        return new ObjectAccessGrant(UUID.randomUUID(), workspaceId, projectId, objectType, targetId, granteeType, granteeId, grantRole, "ACTIVE", null, 0, now, now);
    }
    public ObjectAccessGrant revoke() {
        return new ObjectAccessGrant(id, workspaceId, projectId, objectTypeCode, targetId, granteeType, granteeId, grantRole, "REVOKED", expiresAt, version, createdAt, Instant.now());
    }
}
