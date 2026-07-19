package com.company.scopery.modules.documenthub.attachment.application.response;

import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachment;

import java.time.Instant;
import java.util.UUID;

public record DocumentAttachmentResponse(
        UUID id,
        UUID documentId,
        String blockId,
        String fileName,
        String mediaType,
        Long fileSizeBytes,
        String storageStatus,
        String presignedUrl,
        Instant presignedExpiresAt,
        Instant createdAt
) {
    public static DocumentAttachmentResponse from(DocumentAttachment a) {
        return new DocumentAttachmentResponse(a.id(), a.documentId(), a.blockId(), a.fileName(), a.mediaType(),
                a.fileSizeBytes(), a.storageStatus().name(), a.presignedUrl(), a.presignedExpiresAt(), a.createdAt());
    }
}
