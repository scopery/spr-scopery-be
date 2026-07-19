package com.company.scopery.modules.integrationhub.sync.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateSyncJobRequest(
        @NotNull UUID connectionId,
        @NotBlank String name,
        @NotBlank String syncDirection,
        @NotBlank String syncMode,
        @NotBlank String objectScope,
        @NotBlank String conflictStrategy) {}
