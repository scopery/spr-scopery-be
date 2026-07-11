package com.company.scopery.modules.project.phasedefinition.application.command;

import java.util.UUID;

public record UpdatePhaseDefinitionCommand(
        UUID id,
        String name,
        String description,
        int displayOrder
) {}
