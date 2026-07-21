package com.company.scopery.modules.documenthub.version.domain.model;
import com.company.scopery.modules.documenthub.version.domain.enums.DocumentVersionStatus;
import java.time.Instant; import java.util.UUID;

public record DocumentVersion(
        UUID id, UUID documentId, UUID projectId, UUID workspaceId,
        int versionNumber, String storageKey, String fileName, String contentType,
        Long fileSizeBytes, String checksum, DocumentVersionStatus status,
        String changeNotes, UUID uploadedBy, Instant uploadedAt,
        // Phase 41 — object storage tracking
        String storageProvider, String storageStatus,
        String storageEtag, Instant uploadCompletedAt, Instant storageVerifiedAt,
        int version, Instant createdAt, Instant updatedAt) {

    public static DocumentVersion create(UUID documentId, UUID projectId, UUID workspaceId, int versionNumber, String storageKey,
                                         String fileName, String contentType, Long fileSizeBytes, String checksum, String changeNotes, UUID uploadedBy) {
        return new DocumentVersion(UUID.randomUUID(), documentId, projectId, workspaceId, versionNumber, storageKey, fileName, contentType,
                fileSizeBytes, checksum, DocumentVersionStatus.CURRENT, changeNotes, uploadedBy, Instant.now(),
                null, "NOT_APPLICABLE", null, null, null,
                0, null, null);
    }

    public static DocumentVersion createForPresignedUpload(UUID documentId, UUID projectId, UUID workspaceId, int versionNumber,
                                                           String storageKey, String fileName, String contentType,
                                                           String storageProvider, String changeNotes, UUID uploadedBy) {
        return new DocumentVersion(UUID.randomUUID(), documentId, projectId, workspaceId, versionNumber, storageKey, fileName, contentType,
                null, null, DocumentVersionStatus.PENDING,
                changeNotes, uploadedBy, Instant.now(),
                storageProvider, "PENDING_UPLOAD", null, null, null,
                0, null, null);
    }
}
