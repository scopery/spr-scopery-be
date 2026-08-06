package com.company.scopery.modules.specpack.version.application.command;

import java.util.UUID;

public record RestorePackVersionCommand(
        UUID projectId,
        UUID specPackId,
        UUID versionId
) {}
