package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;
import java.util.UUID;

public record AiActionPreviewResponse(
        UUID previewId,
        UUID planId,
        String previewHash,
        String maskedDiffJson,
        String warningsJson,
        String baselineImpact,
        boolean externalSideEffect,
        Instant validUntil,
        Instant createdAt
) {}
