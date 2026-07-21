package com.company.scopery.modules.aiaction.application.port;

import java.util.Map;
import java.util.UUID;

public record AiActionRequestedAction(
        String toolCode,
        String toolVersion,
        String targetEntityType,
        UUID targetEntityId,
        Map<String, Object> inputArguments
) {}
