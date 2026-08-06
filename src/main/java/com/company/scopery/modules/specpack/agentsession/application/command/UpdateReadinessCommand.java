package com.company.scopery.modules.specpack.agentsession.application.command;

import java.util.UUID;

public record UpdateReadinessCommand(
        UUID projectId,
        UUID sessionId
) {}
