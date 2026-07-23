package com.company.scopery.modules.traceability.screen.application.command;
import java.util.UUID;
public record UpdateRegistryScreenCommand(UUID workspaceId, UUID applicationId, UUID screenId, String name, String routePath) {}
