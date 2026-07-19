package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record DecideDecisionCommand(UUID projectId, UUID decisionId, String outcome) {}
