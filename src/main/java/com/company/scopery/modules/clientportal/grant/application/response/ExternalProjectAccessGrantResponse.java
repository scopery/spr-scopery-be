package com.company.scopery.modules.clientportal.grant.application.response;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrant;
import java.time.Instant; import java.util.UUID;
public record ExternalProjectAccessGrantResponse(UUID id, UUID projectId, UUID portalAccountId, String status, String permissionPolicyCode, Instant expiresAt, Instant createdAt) {
    public static ExternalProjectAccessGrantResponse from(ExternalProjectAccessGrant e) {
        return new ExternalProjectAccessGrantResponse(e.id(), e.projectId(), e.portalAccountId(), e.status().name(), e.permissionPolicyCode(), e.expiresAt(), e.createdAt());
    }
}
