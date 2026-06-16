package com.company.scopery.modules.aiagent.provider.application.query;

public record SearchProviderQuery(
        String keyword,
        String type,
        String status,
        int page,
        int size
) {}