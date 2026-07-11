package com.company.scopery.modules.project.phasedefinition.application.command;

import java.util.UUID;

public record CreateWorkspacePhaseDefinitionCommand(
        UUID workspaceId,
        String code,
        String name,
        String description,
        int displayOrder
) {}
