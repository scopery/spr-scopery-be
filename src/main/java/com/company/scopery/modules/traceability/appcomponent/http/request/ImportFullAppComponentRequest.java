package com.company.scopery.modules.traceability.appcomponent.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ImportFullAppComponentRequest(
        @NotNull @Size(min = 1, max = 200) List<@Valid AppComponentImportItem> items) {

    public record AppComponentImportItem(
            @NotBlank @Size(max = 50) String code,
            @NotBlank @Size(max = 255) String name,
            String description,
            String componentType,
            String optionSourceType,
            UUID sourceEntityId,
            String sourceValueColumn,
            String sourceLabelColumn,
            String sourceFilterJson,
            List<@Valid FieldItem> fields) {

        public record FieldItem(
                @NotBlank String fieldKey,
                @NotBlank String label,
                @NotBlank String fieldType,
                Boolean required,
                Integer maxLength,
                String remark,
                Integer displayOrder) {}
    }
}
