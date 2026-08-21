package com.company.scopery.modules.traceability.application.application.command;

import java.util.UUID;

public record UpdateRegistryApplicationCommand(UUID workspaceId, UUID applicationId, String name, String description) {}
