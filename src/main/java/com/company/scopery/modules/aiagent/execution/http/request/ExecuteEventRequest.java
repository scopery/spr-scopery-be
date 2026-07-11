package com.company.scopery.modules.aiagent.execution.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record ExecuteEventRequest(
        @NotBlank @Size(max = 150) String requestId,
        UUID eventDefinitionId,
        @Size(max = 100) String eventCode,
        @Size(max = 100) String sourceSystem,
        @Size(max = 100) String eventKey,
        @Size(max = 50) String environment,
        @Size(max = 50) String triggerSource,
        Map<String, String> inputVariables,
        @Size(max = 2000) String metadata
) {}
