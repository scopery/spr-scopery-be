package com.company.scopery.modules.integrationhub.importjob.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateImportJobRequest(
        @NotBlank String jobMode,
        @NotBlank String sourceFormat,
        @NotBlank String targetObjectType) {}
