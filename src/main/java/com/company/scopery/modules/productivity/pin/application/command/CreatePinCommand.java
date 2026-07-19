package com.company.scopery.modules.productivity.pin.application.command;
import java.util.UUID;
public record CreatePinCommand(UUID workspaceId, String scope, String targetType, UUID targetId, UUID projectId, Integer sortOrder) {}
