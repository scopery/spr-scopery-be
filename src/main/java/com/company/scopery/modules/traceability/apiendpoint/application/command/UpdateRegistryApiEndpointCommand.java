package com.company.scopery.modules.traceability.apiendpoint.application.command;
import java.util.UUID;
public record UpdateRegistryApiEndpointCommand(UUID workspaceId, UUID applicationId, UUID endpointId, String method, String pathPattern, String name) {}
