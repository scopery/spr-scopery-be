package com.company.scopery.modules.project.projectphase.application.command;

import java.util.UUID;

public record ArchiveProjectPhaseCommand(
        UUID id,
        UUID projectId
) {}
