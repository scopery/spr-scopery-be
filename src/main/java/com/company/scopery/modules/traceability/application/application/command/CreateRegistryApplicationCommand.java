package com.company.scopery.modules.traceability.application.application.command;
import java.util.UUID;
public record CreateRegistryApplicationCommand(UUID workspaceId, String code, String name, String description, UUID ownerUserId) {}
