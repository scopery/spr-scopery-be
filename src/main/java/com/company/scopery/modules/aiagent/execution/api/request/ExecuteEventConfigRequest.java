package com.company.scopery.modules.aiagent.execution.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record ExecuteEventConfigRequest(
        @NotBlank @Size(max = 150) String requestId,
        @Size(max = 50) String triggerSource,
        Map<String, String> inputVariables,
        @Size(max = 2000) String metadata
) {}