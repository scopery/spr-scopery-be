package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record DeleteDecisionOptionCommand(UUID projectId, UUID decisionId, UUID optionId) {}
