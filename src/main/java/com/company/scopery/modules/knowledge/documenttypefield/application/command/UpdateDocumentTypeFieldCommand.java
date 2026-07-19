package com.company.scopery.modules.knowledge.documenttypefield.application.command;

import java.util.UUID;

public record UpdateDocumentTypeFieldCommand(
        UUID documentTypeId,
        UUID fieldId,
        String label,
        String description,
        String dataType,
        boolean required,
        String optionsJson,
        String validationJson,
        String defaultValueJson,
        int displayOrder) {}
