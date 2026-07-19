package com.company.scopery.modules.documenthub.syncedblock.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSyncedBlockRequest(
        @NotBlank String ast,
        Integer schemaVersion
) {}
