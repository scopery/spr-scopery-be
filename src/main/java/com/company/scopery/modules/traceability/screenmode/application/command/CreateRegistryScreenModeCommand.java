package com.company.scopery.modules.traceability.screenmode.application.command;

import java.util.UUID;

public record CreateRegistryScreenModeCommand(
        UUID screenId,
        UUID workspaceId,
        String modeCode,
        String name,
        int displayOrder) {}
