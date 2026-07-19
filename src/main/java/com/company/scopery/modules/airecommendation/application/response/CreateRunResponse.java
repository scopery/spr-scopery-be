package com.company.scopery.modules.airecommendation.application.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateRunResponse(
        UUID runId,
        String status,
        UUID projectId,
        String policyCode,
        List<String> packCodes,
        Map<String, String> links
) {}
