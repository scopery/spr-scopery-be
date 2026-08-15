package com.company.scopery.modules.traceability.dataentity.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ImportFullDataEntityRequest(
        @NotNull @Size(min = 1, max = 200) List<@Valid DataEntityImportItem> items) {

    public record DataEntityImportItem(
            @NotNull UUID projectId,
            @NotBlank @Size(max = 50) String code,
            @NotBlank @Size(max = 255) String name,
            String description,
            String tableName,
            UUID moduleId,
            List<@Valid FieldItem> fields) {

        public record FieldItem(
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
    }
}
