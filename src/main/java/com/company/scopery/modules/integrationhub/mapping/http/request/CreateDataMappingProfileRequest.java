package com.company.scopery.modules.integrationhub.mapping.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateDataMappingProfileRequest(
        @NotBlank String mappingCode,
        @NotBlank String name,
        @NotBlank String targetObjectType,
        @NotBlank String sourceFormat,
        @NotBlank String mappingJson,
        UUID connectionId) {}
