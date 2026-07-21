package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;
import java.util.UUID;

public record AiActionControlCommandResponse(
        UUID commandId,
        UUID executionId,
        String commandType,
        String status,
        Instant createdAt
) {}
