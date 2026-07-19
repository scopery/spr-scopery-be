package com.company.scopery.modules.aiplanning.planningrun.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateAiPlanningRunRequest(
        @NotBlank String runType,
        UUID agentId,
        String promptTemplateCode,
        Map<String, Object> input,
        List<String> includeSections,
        Map<String, Object> options
) {}
