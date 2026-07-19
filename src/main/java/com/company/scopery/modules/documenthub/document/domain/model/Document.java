package com.company.scopery.modules.documenthub.document.domain.model;

import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import java.time.Instant;
import java.util.UUID;

public record Document(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID folderId,
        String documentTypeCode,
        String code,
        String title,
        String description,
        DocumentStatus status,
        String classification,
        UUID currentVersionId,
        boolean locked,
        Instant approvedAt,
        UUID approvedBy,
        Instant archivedAt,
        UUID archivedBy,
        String traceId,
        int version,
        Instant createdAt,
        Instant updatedAt,
        // Native editor fields
        ContentMode contentMode,
        UUID parentDocumentId,
        UUID currentContentRevisionId,
        long currentContentRevisionNo,
        Integer editorSchemaVersion,
        String contentChecksum,
        Instant contentUpdatedAt,
        UUID contentUpdatedBy,
        UUID templateVersionId,
        String pageIcon,
        String pageCoverObjectKey,
        ContentWidth contentWidth,
        boolean clientVisible
) {
    public static Document create(UUID workspaceId, UUID projectId, UUID folderId, String typeCode,
                                   String code, String title, String description, ContentMode contentMode) {
        return new Document(UUID.randomUUID(), workspaceId, projectId, folderId, typeCode, code, title, description,
                DocumentStatus.DRAFT, "INTERNAL", null, false, null, null, null, null, null, 0, null, null,
                contentMode == null ? ContentMode.FILE : contentMode,
                null, null, 0L, null, null, null, null, null, null, null, ContentWidth.CENTERED, false);
    }

    public static Document create(UUID workspaceId, UUID projectId, UUID folderId, String typeCode,
                                   String code, String title, String description) {
        return create(workspaceId, projectId, folderId, typeCode, code, title, description, ContentMode.FILE);
    }

    public Document approve(UUID actorId) {
        if (status == DocumentStatus.APPROVED || status == DocumentStatus.ARCHIVED || status == DocumentStatus.DELETED_SOFT) {
            throw new IllegalStateException("Document cannot be approved in status " + status);
        }
        return new Document(id, workspaceId, projectId, folderId, documentTypeCode, code, title, description,
                DocumentStatus.APPROVED, classification, currentVersionId, locked, Instant.now(), actorId,
                archivedAt, archivedBy, traceId, version, createdAt, Instant.now(),
                contentMode, parentDocumentId, currentContentRevisionId, currentContentRevisionNo,
                editorSchemaVersion, contentChecksum, contentUpdatedAt, contentUpdatedBy,
                templateVersionId, pageIcon, pageCoverObjectKey, contentWidth, clientVisible);
    }

    public Document archive(UUID actorId) {
        return new Document(id, workspaceId, projectId, folderId, documentTypeCode, code, title, description,
                DocumentStatus.ARCHIVED, classification, currentVersionId, locked, approvedAt, approvedBy,
                Instant.now(), actorId, traceId, version, createdAt, Instant.now(),
                contentMode, parentDocumentId, currentContentRevisionId, currentContentRevisionNo,
                editorSchemaVersion, contentChecksum, contentUpdatedAt, contentUpdatedBy,
                templateVersionId, pageIcon, pageCoverObjectKey, contentWidth, clientVisible);
    }

    public Document withCurrentVersion(UUID versionId) {
        return new Document(id, workspaceId, projectId, folderId, documentTypeCode, code, title, description,
                status, classification, versionId, locked, approvedAt, approvedBy, archivedAt, archivedBy,
                traceId, version, createdAt, Instant.now(),
                contentMode, parentDocumentId, currentContentRevisionId, currentContentRevisionNo,
                editorSchemaVersion, contentChecksum, contentUpdatedAt, contentUpdatedBy,
                templateVersionId, pageIcon, pageCoverObjectKey, contentWidth, clientVisible);
    }

    public Document withNativeContentSaved(UUID revisionId, long revisionNo, String checksum,
                                            Integer schemaVersion, UUID savedBy) {
        Instant now = Instant.now();
        return new Document(id, workspaceId, projectId, folderId, documentTypeCode, code, title, description,
                status, classification, currentVersionId, locked, approvedAt, approvedBy, archivedAt, archivedBy,
                traceId, version, createdAt, now,
                contentMode == ContentMode.FILE ? ContentMode.NATIVE : contentMode,
                parentDocumentId, revisionId, revisionNo,
                schemaVersion, checksum, now, savedBy,
                templateVersionId, pageIcon, pageCoverObjectKey, contentWidth, clientVisible);
    }

    public Document withClientVisible(boolean visible) {
        return new Document(id, workspaceId, projectId, folderId, documentTypeCode, code, title, description,
                status, classification, currentVersionId, locked, approvedAt, approvedBy, archivedAt, archivedBy,
                traceId, version, createdAt, Instant.now(),
                contentMode, parentDocumentId, currentContentRevisionId, currentContentRevisionNo,
                editorSchemaVersion, contentChecksum, contentUpdatedAt, contentUpdatedBy,
                templateVersionId, pageIcon, pageCoverObjectKey, contentWidth, visible);
    }

    public boolean isNativeEditable() {
        return contentMode == ContentMode.NATIVE || contentMode == ContentMode.HYBRID;
    }

    public boolean isArchived() {
        return status == DocumentStatus.ARCHIVED || status == DocumentStatus.DELETED_SOFT;
    }
}
