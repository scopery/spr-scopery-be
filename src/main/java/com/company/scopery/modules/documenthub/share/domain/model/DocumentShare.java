package com.company.scopery.modules.documenthub.share.domain.model;
import com.company.scopery.modules.documenthub.share.domain.enums.DocumentShareStatus;
import java.time.Instant; import java.util.UUID;
public record DocumentShare(UUID id, UUID documentId, UUID projectId, String shareType, String granteeType, UUID granteeId,
                            Instant expiresAt, DocumentShareStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static DocumentShare create(UUID documentId, UUID projectId, String shareType, String granteeType, UUID granteeId, Instant expiresAt) {
        Instant now = Instant.now();
        return new DocumentShare(UUID.randomUUID(), documentId, projectId, shareType, granteeType, granteeId, expiresAt, DocumentShareStatus.ACTIVE, 0, now, now);
    }
    public DocumentShare revoke() {
        return new DocumentShare(id, documentId, projectId, shareType, granteeType, granteeId, expiresAt, DocumentShareStatus.REVOKED, version, createdAt, Instant.now());
    }
}
