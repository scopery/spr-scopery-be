package com.company.scopery.modules.documenthub.version.application.response;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersion;
import java.time.Instant; import java.util.UUID;
public record DocumentVersionResponse(UUID id, UUID documentId, int versionNumber, String storageKey, String fileName, String contentType,
                                      Long fileSizeBytes, String status, Instant uploadedAt) {
    public static DocumentVersionResponse from(DocumentVersion e) {
        return new DocumentVersionResponse(e.id(), e.documentId(), e.versionNumber(), e.storageKey(), e.fileName(), e.contentType(),
                e.fileSizeBytes(), e.status().name(), e.uploadedAt());
    }
}
