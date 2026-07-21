package com.company.scopery.modules.aiaction.application.response;

import java.util.List;
import java.util.UUID;

public record AiActionStepResponse(
        UUID stepId,
        int ordinal,
        String toolCode,
        String toolVersion,
        String targetEntityType,
        UUID targetEntityId,
        String riskLevel,
        String executionMode,
        List<UUID> dependsOnStepIds
) {}
