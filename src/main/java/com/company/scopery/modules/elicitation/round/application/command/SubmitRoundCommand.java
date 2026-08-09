package com.company.scopery.modules.elicitation.round.application.command;

import java.util.UUID;

public record SubmitRoundCommand(UUID projectId, UUID sessionId, UUID roundId) {}
