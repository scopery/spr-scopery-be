package com.company.scopery.modules.traceability.screenmode.application.command;

import java.util.UUID;

public record UpdateRegistryScreenModeCommand(
        UUID workspaceId,
        UUID modeId,
        String name,
        int displayOrder) {}
