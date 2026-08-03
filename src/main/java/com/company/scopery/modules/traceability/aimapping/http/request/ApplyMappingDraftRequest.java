package com.company.scopery.modules.traceability.aimapping.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApplyMappingDraftRequest(
        @NotNull UUID runId
) {}
