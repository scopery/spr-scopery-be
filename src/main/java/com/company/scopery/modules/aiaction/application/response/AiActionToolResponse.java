package com.company.scopery.modules.aiaction.application.response;

import java.util.UUID;

public record AiActionToolResponse(
        UUID id,
        String toolCode,
        String toolVersion,
        String invocationScope,
        String riskLevel,
        String executionMode,
        int maxBatchTargets,
        boolean dryRunRequired,
        boolean supportsCompensation,
        boolean supportsPause,
        String status
) {}
