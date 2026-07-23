package com.company.scopery.modules.traceability.dataentity.application.command;
import java.util.UUID;
public record UpdateRegistryDataEntityCommand(UUID workspaceId, UUID dataEntityId, UUID moduleId, String name, String description, String tableName) {}
