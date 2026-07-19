package com.company.scopery.modules.configuration.layout.application.command;
import java.util.UUID;
public record CreateLayoutCommand(UUID workspaceId, String objectType, String layoutType, String name, String layoutJson) {}
