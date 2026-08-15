package com.company.scopery.modules.traceability.dataentityfield.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateRegistryDataEntityFieldRequest(
        @NotBlank String columnName,
        @NotBlank String dataType,
        Integer maxLength,
        boolean isNullable,
        boolean isUnique,
        boolean isPrimaryKey,
        String defaultValue,
        Integer precision,
        Integer scale,
        String remark,
        int displayOrder) {}
