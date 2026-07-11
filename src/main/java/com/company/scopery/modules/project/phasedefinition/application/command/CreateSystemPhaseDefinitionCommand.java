package com.company.scopery.modules.project.phasedefinition.application.command;

public record CreateSystemPhaseDefinitionCommand(
        String code,
        String name,
        String description,
        int displayOrder,
        boolean isSystemDefault
) {}
