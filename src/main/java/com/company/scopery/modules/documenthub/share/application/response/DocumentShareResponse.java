package com.company.scopery.modules.documenthub.share.application.response;
import com.company.scopery.modules.documenthub.share.domain.model.DocumentShare;
import java.time.Instant; import java.util.UUID;
public record DocumentShareResponse(UUID id, UUID documentId, String shareType, String granteeType, UUID granteeId, Instant expiresAt, String status, Instant createdAt) {
    public static DocumentShareResponse from(DocumentShare e) {
        return new DocumentShareResponse(e.id(), e.documentId(), e.shareType(), e.granteeType(), e.granteeId(), e.expiresAt(), e.status().name(), e.createdAt());
    }
}
