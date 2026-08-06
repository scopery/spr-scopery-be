package com.company.scopery.modules.specpack.pack.application.command;

import java.util.UUID;

public record UpdateSpecPackCommand(
        UUID projectId,
        UUID packId,
        String name,
        String description
) {}
