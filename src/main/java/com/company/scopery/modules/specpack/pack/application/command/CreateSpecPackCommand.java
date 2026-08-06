package com.company.scopery.modules.specpack.pack.application.command;

import java.util.UUID;

public record CreateSpecPackCommand(
        UUID projectId,
        String packType,
        String name,
        String description,
        UUID sourcePackId
) {}
