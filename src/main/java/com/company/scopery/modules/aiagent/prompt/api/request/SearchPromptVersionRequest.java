package com.company.scopery.modules.aiagent.prompt.api.request;

import java.util.UUID;

public record SearchPromptVersionRequest(
        UUID templateId,
        String status,
        String contentFormat,
        int page,
        int size
) {}