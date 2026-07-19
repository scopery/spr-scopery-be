package com.company.scopery.modules.knowledge.documenttype.domain.model;

import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;

import java.time.Instant;
import java.util.UUID;

public record DocumentType(
        UUID id,
        DocumentTypeCode code,
        String name,
        String description,
        DocumentTypeScope documentScope,
        DocumentTypeStatus status,
        UUID organizationId,
        UUID workspaceId,
        String category,
        DocumentClassification defaultClassification,
        Integer defaultReviewCycleDays,
        String defaultTemplateCode,
        String metadataSchemaJson,
        boolean builtIn,
        UUID archivedBy,
        Instant archivedAt,
        UUID deletedBy,
        Instant deletedAt,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static DocumentType createSystem(DocumentTypeCode code, String name, String description) {
        return createSystem(code, name, description, null, DocumentClassification.INTERNAL,
                null, null, null, false);
    }

    public static DocumentType createSystem(DocumentTypeCode code, String name, String description,
                                            String category, DocumentClassification defaultClassification,
                                            Integer defaultReviewCycleDays, String defaultTemplateCode,
                                            String metadataSchemaJson, boolean builtIn) {
        return new DocumentType(UUID.randomUUID(), code, name, description,
                DocumentTypeScope.SYSTEM, DocumentTypeStatus.ACTIVE,
                null, null, category,
                defaultClassification != null ? defaultClassification : DocumentClassification.INTERNAL,
                defaultReviewCycleDays, defaultTemplateCode, metadataSchemaJson, builtIn,
                null, null, null, null, 0, null, null);
    }

    public static DocumentType createOrganization(DocumentTypeCode code, String name, String description,
                                                  UUID organizationId, String category,
                                                  DocumentClassification defaultClassification,
                                                  Integer defaultReviewCycleDays, String defaultTemplateCode,
                                                  String metadataSchemaJson) {
        if (organizationId == null) {
            throw new IllegalArgumentException("ORGANIZATION scope requires organizationId");
        }
        Instant now = Instant.now();
        return new DocumentType(UUID.randomUUID(), code, name, description,
                DocumentTypeScope.ORGANIZATION, DocumentTypeStatus.ACTIVE,
                organizationId, null, category,
                defaultClassification != null ? defaultClassification : DocumentClassification.INTERNAL,
                defaultReviewCycleDays, defaultTemplateCode, metadataSchemaJson, false,
                null, null, null, null, 0, now, now);
    }

    public static DocumentType createWorkspace(DocumentTypeCode code, String name, String description,
                                               UUID workspaceId) {
        return createWorkspace(code, name, description, null, workspaceId, null,
                DocumentClassification.INTERNAL, null, null, null);
    }

    public static DocumentType createWorkspace(DocumentTypeCode code, String name, String description,
                                               UUID organizationId, UUID workspaceId, String category,
                                               DocumentClassification defaultClassification,
                                               Integer defaultReviewCycleDays, String defaultTemplateCode,
                                               String metadataSchemaJson) {
        if (workspaceId == null) {
            throw new IllegalArgumentException("WORKSPACE scope requires workspaceId");
        }
        Instant now = Instant.now();
        return new DocumentType(UUID.randomUUID(), code, name, description,
                DocumentTypeScope.WORKSPACE, DocumentTypeStatus.ACTIVE,
                organizationId, workspaceId, category,
                defaultClassification != null ? defaultClassification : DocumentClassification.INTERNAL,
                defaultReviewCycleDays, defaultTemplateCode, metadataSchemaJson, false,
                null, null, null, null, 0, now, now);
    }

    public DocumentType update(String name, String description, String category,
                               DocumentClassification defaultClassification,
                               Integer defaultReviewCycleDays, String defaultTemplateCode,
                               String metadataSchemaJson) {
        if (isArchived()) {
            throw new IllegalStateException("Archived document type cannot be updated");
        }
        return new DocumentType(id, code, name, description, documentScope, status,
                organizationId, workspaceId, category,
                defaultClassification != null ? defaultClassification : this.defaultClassification,
                defaultReviewCycleDays, defaultTemplateCode, metadataSchemaJson, builtIn,
                archivedBy, archivedAt, deletedBy, deletedAt, version + 1, createdAt, Instant.now());
    }

    /** Backward-compatible name/description update. */
    public DocumentType update(String name, String description) {
        return update(name, description, category, defaultClassification,
                defaultReviewCycleDays, defaultTemplateCode, metadataSchemaJson);
    }

    public DocumentType activate() {
        if (isArchived()) {
            throw new IllegalStateException("Archived document type cannot be activated");
        }
        return withStatus(DocumentTypeStatus.ACTIVE);
    }

    public DocumentType deactivate() {
        if (isArchived()) {
            throw new IllegalStateException("Archived document type cannot be deactivated");
        }
        return withStatus(DocumentTypeStatus.INACTIVE);
    }

    public DocumentType archive(UUID actorId) {
        if (isArchived()) {
            return this;
        }
        Instant now = Instant.now();
        return new DocumentType(id, code, name, description, documentScope, DocumentTypeStatus.ARCHIVED,
                organizationId, workspaceId, category, defaultClassification,
                defaultReviewCycleDays, defaultTemplateCode, metadataSchemaJson, builtIn,
                actorId, now, actorId, now, version + 1, createdAt, now);
    }

    /** @deprecated Prefer {@link #archive(UUID)}; kept as soft-delete alias. */
    @Deprecated
    public DocumentType softDelete(UUID actorId) {
        return archive(actorId);
    }

    public boolean isSystem() {
        return documentScope == DocumentTypeScope.SYSTEM;
    }

    public boolean isOrganization() {
        return documentScope == DocumentTypeScope.ORGANIZATION;
    }

    public boolean isWorkspace() {
        return documentScope == DocumentTypeScope.WORKSPACE;
    }

    public boolean isArchived() {
        return status == DocumentTypeStatus.ARCHIVED || archivedAt != null;
    }

    /** @deprecated Prefer {@link #isArchived()}. */
    @Deprecated
    public boolean isDeleted() {
        return isArchived();
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    private DocumentType withStatus(DocumentTypeStatus newStatus) {
        return new DocumentType(id, code, name, description, documentScope, newStatus,
                organizationId, workspaceId, category, defaultClassification,
                defaultReviewCycleDays, defaultTemplateCode, metadataSchemaJson, builtIn,
                archivedBy, archivedAt, deletedBy, deletedAt, version + 1, createdAt, Instant.now());
    }
}
