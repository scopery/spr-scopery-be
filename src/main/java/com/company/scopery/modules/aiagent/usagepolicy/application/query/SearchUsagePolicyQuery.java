package com.company.scopery.modules.aiagent.usagepolicy.application.query;

public record SearchUsagePolicyQuery(
        String keyword,
        String targetType,
        String status,
        int page,
        int size
) {}