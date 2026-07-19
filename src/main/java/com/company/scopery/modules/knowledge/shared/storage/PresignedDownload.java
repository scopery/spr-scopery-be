package com.company.scopery.modules.knowledge.shared.storage;

import java.time.Instant;

public record PresignedDownload(
        String downloadUrl,
        String objectKey,
        Instant expiresAt
) {}
