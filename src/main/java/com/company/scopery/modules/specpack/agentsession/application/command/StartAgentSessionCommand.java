package com.company.scopery.modules.specpack.agentsession.application.command;

import java.util.UUID;

public record StartAgentSessionCommand(
        UUID projectId,
        UUID specPackId,
        UUID scopePackageId
) {}
