package com.company.scopery.modules.traceability.componentapi.application.command;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;

import java.util.UUID;

public record UpdateRegistryComponentApiCommand(
        UUID workspaceId, UUID componentApiId,
        ComponentApiRole role, String note, int displayOrder) {}
