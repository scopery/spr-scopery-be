package com.company.scopery.modules.airecommendation.application.response;

import java.util.Map;
import java.util.UUID;

public record PrepareApplyResponse(
        boolean available,
        String suggestionRef,
        UUID actionRequestId,
        UUID actionPlanId,
        String planStatus,
        boolean confirmationRequired,
        String expiresAt,
        Map<String, String> links
) {}
