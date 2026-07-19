package com.company.scopery.modules.project.phasedefinition.application.command;

import java.util.UUID;

public record CreateOrganizationPhaseDefinitionCommand(
        UUID organizationId,
        String code,
        String name,
        String description,
        int displayOrder
) {}
