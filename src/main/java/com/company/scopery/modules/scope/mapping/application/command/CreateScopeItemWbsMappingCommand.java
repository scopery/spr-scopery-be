package com.company.scopery.modules.scope.mapping.application.command;
import java.util.UUID;
public record CreateScopeItemWbsMappingCommand(UUID projectId, UUID scopeItemId, UUID wbsNodeId, String mappingType) {}
