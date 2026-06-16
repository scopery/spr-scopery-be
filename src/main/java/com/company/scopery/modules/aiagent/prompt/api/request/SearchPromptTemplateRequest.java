package com.company.scopery.modules.aiagent.prompt.api.request;

import java.util.UUID;

public record SearchPromptTemplateRequest(
        UUID agentId,
        String keyword,
        String status,
        int page,
        int size
) {}