package com.company.scopery.modules.integrationhub.credential.application.response;
import com.company.scopery.modules.integrationhub.credential.domain.model.CredentialReference;
import java.time.Instant; import java.util.UUID;
/** Never includes raw secret value. */
public record CredentialReferenceResponse(UUID id, UUID workspaceId, String providerCode, String credentialType,
        String status, String secretReferenceMasked, Instant lastRotatedAt, Instant revokedAt,
        Instant createdAt, Instant updatedAt) {
    public static CredentialReferenceResponse from(CredentialReference c) {
        String masked = c.secretReference() == null ? null : "ref:" + Math.abs(c.secretReference().hashCode());
        return new CredentialReferenceResponse(c.id(), c.workspaceId(), c.providerCode(), c.credentialType(),
                c.status(), masked, c.lastRotatedAt(), c.revokedAt(), c.createdAt(), c.updatedAt());
    }
}
