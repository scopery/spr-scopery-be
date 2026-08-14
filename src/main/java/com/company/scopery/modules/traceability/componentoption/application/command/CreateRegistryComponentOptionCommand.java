package com.company.scopery.modules.traceability.componentoption.application.command;

import java.util.UUID;

public record CreateRegistryComponentOptionCommand(
        UUID workspaceId,
        UUID componentId,
        String optionValue,
        String optionLabel,
        int displayOrder
) {}
