package com.company.scopery.modules.traceability.screencomponent.application.command;

import java.util.UUID;

public record BindComponentToSectionCommand(
        UUID workspaceId, UUID screenId, UUID sectionId, UUID componentId,
        int displayOrder, String note) {}
