package com.company.scopery.modules.aiagent.prompt.application.query;

import java.util.UUID;

public record SearchPromptTemplateQuery(
        UUID agentId,
        String keyword,
        String status,
        int page,
        int size
) {}
