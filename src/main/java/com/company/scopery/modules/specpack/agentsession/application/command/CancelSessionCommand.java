package com.company.scopery.modules.specpack.agentsession.application.command;

import java.util.UUID;

public record CancelSessionCommand(
        UUID projectId,
        UUID sessionId
) {}
