package com.company.scopery.modules.integrationhub.exportprofile.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateExportProfileRequest(
        @NotBlank String profileCode,
        @NotBlank String name,
        @NotBlank String exportFormat,
        @NotBlank String targetDestination,
        @NotBlank String objectScope,
        UUID connectionId) {}
