package com.company.scopery.modules.aiagent.playground.api.request;

import jakarta.validation.constraints.Size;

import java.util.Map;

public record RunPlaygroundEventConfigRequest(
        @Size(max = 128) String requestId,
        Map<String, String> inputVariables
) {}
