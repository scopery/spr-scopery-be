package com.company.scopery.modules.documenthub.attachment.domain.model;

import com.company.scopery.modules.documenthub.attachment.domain.enums.AttachmentStorageStatus;

import java.time.Instant;
import java.util.UUID;

public record DocumentAttachment(
        UUID id,
        UUID documentId,
        UUID workspaceId,
        UUID projectId,
        String blockId,
        String fileName,
        String mediaType,
        Long fileSizeBytes,
        String objectKey,
        AttachmentStorageStatus storageStatus,
        String checksum,
        String presignedUrl,
        Instant presignedExpiresAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentAttachment createPendingUpload(UUID documentId, UUID workspaceId, UUID projectId,
                                                          String blockId, String fileName, String mediaType,
                                                          Long fileSizeBytes, String objectKey,
                                                          String presignedUrl, Instant presignedExpiresAt) {
        Instant now = Instant.now();
        return new DocumentAttachment(UUID.randomUUID(), documentId, workspaceId, projectId,
                blockId, fileName, mediaType, fileSizeBytes, objectKey,
                AttachmentStorageStatus.PENDING_UPLOAD, null, presignedUrl, presignedExpiresAt, now, now);
    }

    public DocumentAttachment markAvailable(String checksum, Long sizeBytes) {
        return new DocumentAttachment(id, documentId, workspaceId, projectId, blockId, fileName, mediaType,
                sizeBytes != null ? sizeBytes : fileSizeBytes, objectKey,
                AttachmentStorageStatus.AVAILABLE, checksum, null, null, createdAt, Instant.now());
    }

    public DocumentAttachment markFailed() {
        return new DocumentAttachment(id, documentId, workspaceId, projectId, blockId, fileName, mediaType,
                fileSizeBytes, objectKey, AttachmentStorageStatus.FAILED, checksum, null, null, createdAt, Instant.now());
    }
}
