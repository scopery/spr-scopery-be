package com.company.scopery.modules.documenthub.version.application.response;

import java.time.Instant;
import java.util.UUID;

public record PresignedUploadResponse(
        UUID versionId,
        String uploadUrl,
        String objectKey,
        String storageProvider,
        Instant expiresAt
) {}
