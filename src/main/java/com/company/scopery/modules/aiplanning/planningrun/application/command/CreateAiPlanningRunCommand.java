package com.company.scopery.modules.aiplanning.planningrun.application.command;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateAiPlanningRunCommand(
        UUID projectId,
        String runType,
        UUID agentId,
        String promptTemplateCode,
        Map<String, Object> input,
        List<String> includeSections,
        Map<String, Object> options
) {}
