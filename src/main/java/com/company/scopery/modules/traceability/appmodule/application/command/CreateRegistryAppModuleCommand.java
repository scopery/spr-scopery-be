package com.company.scopery.modules.traceability.appmodule.application.command;
import java.util.UUID;
public record CreateRegistryAppModuleCommand(UUID applicationId, UUID workspaceId, String code, String name, String description) {}
