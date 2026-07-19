package com.company.scopery.modules.productivity.favorite.application.command;
import java.util.UUID;
public record CreateFavoriteCommand(UUID workspaceId, String targetType, UUID targetId, String label) {}
