package com.company.scopery.modules.elicitation.round.application.command;

import java.util.UUID;

public record GenerateRoundCommand(UUID projectId, UUID sessionId) {}
