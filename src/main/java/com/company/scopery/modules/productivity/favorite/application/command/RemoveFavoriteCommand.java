package com.company.scopery.modules.productivity.favorite.application.command;
import java.util.UUID;
public record RemoveFavoriteCommand(UUID workspaceId, UUID favoriteId) {}
