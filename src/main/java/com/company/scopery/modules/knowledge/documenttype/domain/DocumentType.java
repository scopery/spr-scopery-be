package com.company.scopery.modules.knowledge.documenttype.domain;

import java.time.Instant;
import java.util.UUID;

public record DocumentType(
        UUID id,
        DocumentTypeCode code,
        String name,
        String description,
        DocumentTypeScope documentScope,
        DocumentTypeStatus status,
        UUID workspaceId,
        UUID deletedBy,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static DocumentType createSystem(DocumentTypeCode code, String name, String description) {
        Instant now = Instant.now();
        return new DocumentType(UUID.randomUUID(), code, name, description,
                DocumentTypeScope.SYSTEM, DocumentTypeStatus.ACTIVE,
                null, null, null, now, now);
    }

    public static DocumentType createWorkspace(DocumentTypeCode code, String name, String description,
                                               UUID workspaceId) {
        Instant now = Instant.now();
        return new DocumentType(UUID.randomUUID(), code, name, description,
                DocumentTypeScope.WORKSPACE, DocumentTypeStatus.ACTIVE,
                workspaceId, null, null, now, now);
    }

    public DocumentType update(String name, String description) {
        return new DocumentType(id, code, name, description, documentScope, status,
                workspaceId, deletedBy, deletedAt, createdAt, Instant.now());
    }

    public DocumentType activate() {
        return new DocumentType(id, code, name, description, documentScope, DocumentTypeStatus.ACTIVE,
                workspaceId, deletedBy, deletedAt, createdAt, Instant.now());
    }

    public DocumentType deactivate() {
        return new DocumentType(id, code, name, description, documentScope, DocumentTypeStatus.INACTIVE,
                workspaceId, deletedBy, deletedAt, createdAt, Instant.now());
    }

    public DocumentType softDelete(UUID actorId) {
        Instant now = Instant.now();
        return new DocumentType(id, code, name, description, documentScope, DocumentTypeStatus.DELETED,
                workspaceId, actorId, now, createdAt, now);
    }

    public boolean isSystem() {
        return documentScope == DocumentTypeScope.SYSTEM;
    }

    public boolean isDeleted() {
        return status == DocumentTypeStatus.DELETED;
    }
}
