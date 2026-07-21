package com.company.scopery.modules.aiaction.application.port;

import java.util.List;

public record AiActionDryRunResult(
        boolean success,
        List<String> warnings,
        String baselineImpact,
        boolean externalSideEffect,
        String maskedDiffJson
) {}
