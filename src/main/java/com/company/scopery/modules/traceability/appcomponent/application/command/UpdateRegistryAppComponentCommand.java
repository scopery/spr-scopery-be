package com.company.scopery.modules.traceability.appcomponent.application.command;
import java.util.UUID;
public record UpdateRegistryAppComponentCommand(UUID workspaceId, UUID appComponentId, String name, String description, String componentType) {}
