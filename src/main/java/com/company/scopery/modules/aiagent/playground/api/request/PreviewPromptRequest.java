package com.company.scopery.modules.aiagent.playground.api.request;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PreviewPromptRequest(
        @NotNull UUID promptVersionId,
        Map<String, String> inputVariables
) {}
