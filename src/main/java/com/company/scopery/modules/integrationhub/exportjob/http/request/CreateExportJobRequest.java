package com.company.scopery.modules.integrationhub.exportjob.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateExportJobRequest(
        @NotBlank String exportFormat,
        @NotBlank String objectScope,
        UUID projectId,
        UUID exportProfileId,
        Long rowCount,
        String reason) {}
