package com.company.scopery.modules.integrationhub.sync.http.request;
import jakarta.validation.constraints.NotBlank;
public record ResolveSyncConflictRequest(
        @NotBlank String resolutionStrategy,
        String resolutionNotes) {}
