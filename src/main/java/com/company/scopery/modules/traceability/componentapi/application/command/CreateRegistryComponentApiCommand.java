package com.company.scopery.modules.traceability.componentapi.application.command;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;

import java.util.UUID;

public record CreateRegistryComponentApiCommand(
        UUID workspaceId, UUID componentId, UUID apiId,
        ComponentApiRole role, String note, int displayOrder) {}
