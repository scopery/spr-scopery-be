package com.company.scopery.modules.project.projectphase.application.command;

import java.util.UUID;

public record ActivateProjectPhaseCommand(
        UUID id,
        UUID projectId
) {}
