package com.company.scopery.modules.airecommendation.application.response;

import java.util.Map;

public record NextBestActionItemResponse(
        String code,
        String label,
        String actionKind,
        boolean enabled,
        String riskLevel,
        String requiredAuthorityCode,
        String phase44ToolCode,
        String disabledReasonCode,
        Map<String, Object> metadata
) {}
