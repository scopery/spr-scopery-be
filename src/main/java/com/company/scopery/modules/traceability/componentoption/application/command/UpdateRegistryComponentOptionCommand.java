package com.company.scopery.modules.traceability.componentoption.application.command;

import java.util.UUID;

public record UpdateRegistryComponentOptionCommand(
        UUID workspaceId,
        UUID componentId,
        UUID optionId,
        String optionValue,
        String optionLabel,
        int displayOrder
) {}
