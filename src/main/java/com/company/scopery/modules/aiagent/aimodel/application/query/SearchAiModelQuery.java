package com.company.scopery.modules.aiagent.aimodel.application.query;

import java.util.UUID;

public record SearchAiModelQuery(
        UUID providerId,
        String keyword,
        String status,
        String type,
        int page,
        int size
) {}
