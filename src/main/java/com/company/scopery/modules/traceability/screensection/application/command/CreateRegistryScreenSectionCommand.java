package com.company.scopery.modules.traceability.screensection.application.command;
import java.util.UUID;
public record CreateRegistryScreenSectionCommand(UUID screenId, UUID workspaceId, String name, String description, int displayOrder) {}
