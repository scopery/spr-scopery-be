package com.company.scopery.modules.traceability.screen.application.command;
import java.util.UUID;
public record CreateRegistryScreenCommand(UUID workspaceId, UUID applicationId, UUID projectId, String code, String name, String routePath) {}
