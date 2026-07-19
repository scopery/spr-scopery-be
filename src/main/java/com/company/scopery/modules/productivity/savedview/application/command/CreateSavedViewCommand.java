package com.company.scopery.modules.productivity.savedview.application.command;
import java.util.UUID;
public record CreateSavedViewCommand(UUID workspaceId, String targetType, String name, UUID projectId, String viewConfigJson, String filtersJson) {}
