package com.company.scopery.modules.project.projectphase.application.command;

import java.util.UUID;

public record CompleteProjectPhaseCommand(
        UUID id,
        UUID projectId
) {}
