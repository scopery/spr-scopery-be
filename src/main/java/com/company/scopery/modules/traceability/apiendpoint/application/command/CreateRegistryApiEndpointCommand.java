package com.company.scopery.modules.traceability.apiendpoint.application.command;
import java.util.UUID;
public record CreateRegistryApiEndpointCommand(UUID workspaceId, UUID applicationId, UUID projectId, String method, String pathPattern, String name) {}
