package com.company.scopery.modules.knowledge.documenttypefield.application.response;

import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;

import java.time.Instant;
import java.util.UUID;

public record DocumentTypeFieldResponse(
        UUID id,
        UUID documentTypeId,
        String fieldKey,
        String label,
        String description,
        String dataType,
        boolean required,
        boolean systemField,
        String optionsJson,
        String validationJson,
        String defaultValueJson,
        int displayOrder,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static DocumentTypeFieldResponse from(DocumentTypeField field) {
        return new DocumentTypeFieldResponse(
                field.id(),
                field.documentTypeId(),
                field.fieldKey().value(),
                field.label(),
                field.description(),
                field.dataType().name(),
                field.required(),
                field.systemField(),
                field.optionsJson(),
                field.validationJson(),
                field.defaultValueJson(),
                field.displayOrder(),
                field.status().name(),
                field.version(),
                field.createdAt(),
                field.updatedAt());
    }
}
