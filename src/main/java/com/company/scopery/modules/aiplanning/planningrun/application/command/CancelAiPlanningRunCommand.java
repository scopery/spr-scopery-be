package com.company.scopery.modules.aiplanning.planningrun.application.command;

import java.util.UUID;

public record CancelAiPlanningRunCommand(UUID projectId, UUID runId) {}
