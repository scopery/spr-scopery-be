package com.company.scopery.modules.documenthub.syncedblock.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSyncedBlockRequest(
        String title,
        @NotBlank String ast,
        Integer schemaVersion
) {}
