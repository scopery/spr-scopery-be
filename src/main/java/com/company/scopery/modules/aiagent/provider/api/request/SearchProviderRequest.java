package com.company.scopery.modules.aiagent.provider.api.request;

public record SearchProviderRequest(
        String keyword,
        String type,
        String status,
        int page,
        int size
) {}
