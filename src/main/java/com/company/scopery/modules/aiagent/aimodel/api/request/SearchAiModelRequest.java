package com.company.scopery.modules.aiagent.aimodel.api.request;

import java.util.UUID;

public record SearchAiModelRequest(
        UUID providerId,
        String keyword,
        String status,
        String type,
        int page,
        int size
) {}