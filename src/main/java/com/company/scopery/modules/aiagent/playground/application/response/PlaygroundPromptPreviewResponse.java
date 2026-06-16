package com.company.scopery.modules.aiagent.playground.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PlaygroundPromptPreviewResponse(
        UUID promptVersionId,
        UUID promptTemplateId,
        String renderedPrompt,
        List<String> missingVariables,
        Instant renderedAt
) {}
