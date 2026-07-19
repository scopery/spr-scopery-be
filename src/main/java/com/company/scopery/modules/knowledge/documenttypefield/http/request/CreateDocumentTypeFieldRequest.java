package com.company.scopery.modules.knowledge.documenttypefield.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDocumentTypeFieldRequest(
        @NotBlank @Size(max = 100) String fieldKey,
        @NotBlank @Size(max = 255) String label,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 50) String dataType,
        boolean required,
        boolean systemField,
        String optionsJson,
        String validationJson,
        String defaultValueJson,
        int displayOrder) {}
