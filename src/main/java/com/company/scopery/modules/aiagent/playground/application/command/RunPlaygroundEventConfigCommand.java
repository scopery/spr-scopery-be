package com.company.scopery.modules.aiagent.playground.application.command;

import java.util.Map;
import java.util.UUID;

public record RunPlaygroundEventConfigCommand(
        String requestId,
        UUID eventConfigId,
        Map<String, String> inputVariables
) {}
