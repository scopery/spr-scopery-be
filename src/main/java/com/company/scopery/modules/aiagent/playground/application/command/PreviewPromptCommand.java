package com.company.scopery.modules.aiagent.playground.application.command;

import java.util.Map;
import java.util.UUID;

public record PreviewPromptCommand(
        UUID promptVersionId,
        Map<String, String> inputVariables
) {}
