package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import java.util.UUID;

public record UsagePolicyViolation(
        UUID policyId,
        String policyCode,
        String targetType,
        String period,
        String metricName,
        String limitValue,
        String currentValue,
        String action,
        String message
) {}
