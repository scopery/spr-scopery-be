package com.company.scopery.modules.specpack.version.application.command;

import java.util.UUID;

public record CreatePackVersionCommand(
        UUID projectId,
        UUID specPackId,
        String changeReason
) {}
