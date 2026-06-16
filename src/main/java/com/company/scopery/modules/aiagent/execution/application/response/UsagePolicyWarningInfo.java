package com.company.scopery.modules.aiagent.execution.application.response;

public record UsagePolicyWarningInfo(
        String policyCode,
        String targetType,
        String period,
        String metricName,
        String limitValue,
        String currentValue,
        String message
) {}
