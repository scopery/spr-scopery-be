package com.company.scopery.modules.knowledge.documenttypefield.application.command;

import java.util.UUID;

public record CreateDocumentTypeFieldCommand(
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
        int displayOrder) {}
