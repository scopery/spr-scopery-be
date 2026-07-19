package com.company.scopery.modules.integrationhub.mapping.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateDataMappingProfileRequest(
        @NotBlank String name,
        @NotBlank String mappingJson,
        String validationRulesJson) {}
