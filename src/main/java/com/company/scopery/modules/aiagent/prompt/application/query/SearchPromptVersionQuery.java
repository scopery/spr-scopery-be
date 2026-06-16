package com.company.scopery.modules.aiagent.prompt.application.query;

import java.util.UUID;

public record SearchPromptVersionQuery(
        UUID templateId,
        String status,
        String contentFormat,
        int page,
        int size
) {}