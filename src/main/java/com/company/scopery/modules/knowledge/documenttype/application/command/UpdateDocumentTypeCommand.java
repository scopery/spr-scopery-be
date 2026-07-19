package com.company.scopery.modules.knowledge.documenttype.application.command;

import java.util.UUID;

public record UpdateDocumentTypeCommand(
        UUID id,
        String name,
        String description,
        String category,
        String defaultClassification,
        Integer defaultReviewCycleDays,
        String defaultTemplateCode,
        String metadataSchemaJson) {

    public UpdateDocumentTypeCommand(UUID id, String name, String description) {
        this(id, name, description, null, null, null, null, null);
    }
}
