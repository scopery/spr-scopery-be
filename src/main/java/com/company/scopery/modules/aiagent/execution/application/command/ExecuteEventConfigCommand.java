package com.company.scopery.modules.aiagent.execution.application.command;

import java.util.Map;
import java.util.UUID;

public record ExecuteEventConfigCommand(
        String requestId,
        UUID eventConfigId,
        String triggerSource,
        Map<String, String> inputVariables
) {}
