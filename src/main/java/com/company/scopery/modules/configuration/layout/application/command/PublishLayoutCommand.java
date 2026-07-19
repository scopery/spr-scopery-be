package com.company.scopery.modules.configuration.layout.application.command;
import java.util.UUID;
public record PublishLayoutCommand(UUID workspaceId, UUID layoutId) {}
