package com.company.scopery.modules.productivity.recent.application.command;
import java.util.UUID;
public record RecordRecentItemCommand(UUID workspaceId, String targetType, UUID targetId, String title) {}
