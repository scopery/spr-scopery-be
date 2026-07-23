package com.company.scopery.modules.traceability.screencomponent.application.command;

import java.util.UUID;

public record LinkScreenComponentCommand(
        UUID workspaceId,
        UUID screenId,
        UUID componentId,
        UUID sectionId,
        int displayOrder,
        String note) {}
