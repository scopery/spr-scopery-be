package com.company.scopery.modules.integrationhub.credential.domain.model;
import java.time.Instant; import java.util.UUID;
public record CredentialReference(UUID id, UUID workspaceId, String providerCode, String credentialType,
        String secretReference, String status, Instant lastRotatedAt, Instant revokedAt, int version, Instant createdAt, Instant updatedAt) {
    public static CredentialReference create(UUID workspaceId, String providerCode, String type, String secretRef) {
        Instant now = Instant.now();
        return new CredentialReference(UUID.randomUUID(), workspaceId, providerCode, type, secretRef, "ACTIVE", null, null, 0, now, now);
    }
    public CredentialReference rotate(String newSecretRef) {
        return new CredentialReference(id, workspaceId, providerCode, credentialType, newSecretRef, "ACTIVE", Instant.now(), null, version, createdAt, Instant.now());
    }
    public CredentialReference revoke() {
        return new CredentialReference(id, workspaceId, providerCode, credentialType, secretReference, "REVOKED", lastRotatedAt, Instant.now(), version, createdAt, Instant.now());
    }
}
