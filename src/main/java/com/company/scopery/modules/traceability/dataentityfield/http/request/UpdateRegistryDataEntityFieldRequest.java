package com.company.scopery.modules.traceability.dataentityfield.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRegistryDataEntityFieldRequest(
        @NotBlank String columnName,
        @NotBlank String dataType,
        Integer maxLength,
        boolean isNullable,
        boolean isUnique,
        String remark,
        int displayOrder) {}
