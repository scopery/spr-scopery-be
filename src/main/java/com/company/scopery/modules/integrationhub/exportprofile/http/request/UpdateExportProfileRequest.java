package com.company.scopery.modules.integrationhub.exportprofile.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateExportProfileRequest(
        @NotBlank String name,
        String columnsJson,
        String filtersJson,
        String maskingPolicy) {}
