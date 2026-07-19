package com.company.scopery.modules.configuration.tag.application.command;
import java.util.UUID;
public record AssignTagCommand(UUID workspaceId, UUID tagId, String objectType, UUID targetId) {}
