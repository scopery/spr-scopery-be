package com.company.scopery.modules.traceability.screenaction.application.command;
import java.util.UUID;
public record UpdateRegistryScreenActionCommand(UUID workspaceId, UUID actionId, String name, String actionType, String description, int displayOrder) {}
