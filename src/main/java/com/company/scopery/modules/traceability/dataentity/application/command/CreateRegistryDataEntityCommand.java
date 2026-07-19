package com.company.scopery.modules.traceability.dataentity.application.command;
import java.util.UUID;
public record CreateRegistryDataEntityCommand(UUID applicationId, UUID workspaceId, String code, String name, String description, String tableName) {}
