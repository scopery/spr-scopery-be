package com.company.scopery.modules.knowledge.documenttypefield.domain.model;

import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldDataType;
import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldStatus;
import com.company.scopery.modules.knowledge.documenttypefield.domain.valueobject.DocumentTypeFieldKey;

import java.time.Instant;
import java.util.UUID;

public record DocumentTypeField(
        UUID id,
        UUID documentTypeId,
        DocumentTypeFieldKey fieldKey,
        String label,
        String description,
        DocumentTypeFieldDataType dataType,
        boolean required,
        boolean systemField,
        String optionsJson,
        String validationJson,
        String defaultValueJson,
        int displayOrder,
        DocumentTypeFieldStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static DocumentTypeField create(UUID documentTypeId, DocumentTypeFieldKey fieldKey, String label,
                                           String description, DocumentTypeFieldDataType dataType,
                                           boolean required, boolean systemField, String optionsJson,
                                           String validationJson, String defaultValueJson, int displayOrder) {
        return new DocumentTypeField(UUID.randomUUID(), documentTypeId, fieldKey, label, description, dataType,
                required, systemField, optionsJson, validationJson, defaultValueJson,
                Math.max(displayOrder, 0), DocumentTypeFieldStatus.ACTIVE, 0, null, null);
    }

    public DocumentTypeField update(String label, String description, DocumentTypeFieldDataType dataType,
                                    boolean required, String optionsJson, String validationJson,
                                    String defaultValueJson, int displayOrder) {
        if (isArchived()) {
            throw new IllegalStateException("Archived field cannot be updated");
        }
        return new DocumentTypeField(id, documentTypeId, fieldKey, label, description, dataType,
                required, systemField, optionsJson, validationJson, defaultValueJson,
                Math.max(displayOrder, 0), status, version + 1, createdAt, Instant.now());
    }

    public DocumentTypeField withDisplayOrder(int order) {
        return new DocumentTypeField(id, documentTypeId, fieldKey, label, description, dataType,
                required, systemField, optionsJson, validationJson, defaultValueJson,
                Math.max(order, 0), status, version + 1, createdAt, Instant.now());
    }

    public DocumentTypeField activate() {
        if (isArchived()) {
            throw new IllegalStateException("Archived field cannot be activated");
        }
        return withStatus(DocumentTypeFieldStatus.ACTIVE);
    }

    public DocumentTypeField deactivate() {
        if (isArchived()) {
            throw new IllegalStateException("Archived field cannot be deactivated");
        }
        return withStatus(DocumentTypeFieldStatus.INACTIVE);
    }

    public DocumentTypeField archive() {
        if (systemField) {
            throw new IllegalStateException("System field cannot be archived");
        }
        return withStatus(DocumentTypeFieldStatus.ARCHIVED);
    }

    public boolean isArchived() {
        return status == DocumentTypeFieldStatus.ARCHIVED;
    }

    private DocumentTypeField withStatus(DocumentTypeFieldStatus newStatus) {
        return new DocumentTypeField(id, documentTypeId, fieldKey, label, description, dataType,
                required, systemField, optionsJson, validationJson, defaultValueJson,
                displayOrder, newStatus, version + 1, createdAt, Instant.now());
    }
}
