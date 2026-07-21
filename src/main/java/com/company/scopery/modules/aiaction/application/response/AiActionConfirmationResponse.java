package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;
import java.util.UUID;

public record AiActionConfirmationResponse(
        UUID confirmationId,
        UUID planId,
        String planHash,
        String decision,
        String status,
        Instant expiresAt,
        Instant createdAt
) {}
