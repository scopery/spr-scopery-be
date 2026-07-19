package com.company.scopery.modules.knowledge.documenttype.application.command;

import java.util.UUID;

public record CreateDocumentTypeCommand(
        String code,
        String name,
        String description,
        String documentScope,
        UUID organizationId,
        UUID workspaceId,
        String category,
        String defaultClassification,
        Integer defaultReviewCycleDays,
        String defaultTemplateCode,
        String metadataSchemaJson) {

    /** Backward-compatible constructor used by legacy /system and /workspace endpoints. */
    public CreateDocumentTypeCommand(String code, String name, String description,
                                     String documentScope, UUID workspaceId) {
        this(code, name, description, documentScope, null, workspaceId, null, null, null, null, null);
    }
}
