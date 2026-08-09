package com.company.scopery.modules.elicitation.session.application.command;

import java.util.UUID;

public record CloseSessionCommand(
        UUID projectId,
        UUID sessionId
) {}
