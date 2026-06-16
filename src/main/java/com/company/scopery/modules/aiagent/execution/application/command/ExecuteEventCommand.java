package com.company.scopery.modules.aiagent.execution.application.command;

import java.util.Map;
import java.util.UUID;

public record ExecuteEventCommand(
        String requestId,
        UUID eventDefinitionId,
        String eventCode,
        String sourceSystem,
        String eventKey,
        String environment,
        String triggerSource,
        Map<String, String> inputVariables
) {}
