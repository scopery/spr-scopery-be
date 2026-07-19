package com.company.scopery.modules.knowledge.shared.storage;

import java.time.Instant;

public record PresignedUpload(
        String uploadUrl,
        String objectKey,
        Instant expiresAt
) {}
