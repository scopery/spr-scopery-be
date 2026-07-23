package com.company.scopery.modules.traceability.appmodule.application.command;
import java.util.UUID;
public record UpdateRegistryAppModuleCommand(UUID workspaceId, UUID appModuleId, String name, String description) {}
