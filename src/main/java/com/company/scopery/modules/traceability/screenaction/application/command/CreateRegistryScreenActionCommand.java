package com.company.scopery.modules.traceability.screenaction.application.command;
import java.util.UUID;
public record CreateRegistryScreenActionCommand(UUID screenId, UUID workspaceId, String actionCode, String name,
                                                String actionType, String description, int displayOrder) {}
