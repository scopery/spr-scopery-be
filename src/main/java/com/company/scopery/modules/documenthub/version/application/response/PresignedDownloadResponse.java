package com.company.scopery.modules.documenthub.version.application.response;

import java.time.Instant;
import java.util.UUID;

public record PresignedDownloadResponse(
        UUID versionId,
        String downloadUrl,
        String objectKey,
        Instant expiresAt
) {}
