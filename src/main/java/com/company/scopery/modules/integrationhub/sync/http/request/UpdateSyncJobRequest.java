package com.company.scopery.modules.integrationhub.sync.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateSyncJobRequest(
        @NotBlank String name,
        @NotBlank String syncDirection,
        @NotBlank String syncMode,
        @NotBlank String objectScope,
        @NotBlank String conflictStrategy) {}
