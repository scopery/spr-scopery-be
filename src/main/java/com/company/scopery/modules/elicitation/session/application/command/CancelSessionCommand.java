package com.company.scopery.modules.elicitation.session.application.command;

import java.util.UUID;

public record CancelSessionCommand(
        UUID projectId,
        UUID sessionId
) {}
