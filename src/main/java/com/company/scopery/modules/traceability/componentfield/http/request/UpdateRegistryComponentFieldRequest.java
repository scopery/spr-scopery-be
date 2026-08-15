package com.company.scopery.modules.traceability.componentfield.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRegistryComponentFieldRequest(
        @NotBlank String label,
        @NotBlank String fieldType,
        Boolean required,
        Integer maxLength,
        String remark,
        Integer displayOrder) {}
