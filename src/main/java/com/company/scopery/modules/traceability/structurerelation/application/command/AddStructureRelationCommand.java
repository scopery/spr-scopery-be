package com.company.scopery.modules.traceability.structurerelation.application.command;

import java.util.UUID;

public record AddStructureRelationCommand(
        UUID applicationId,
        UUID workspaceId,
        String fromNodeType,
        UUID fromNodeId,
        String toNodeType,
        UUID toNodeId,
        String relationType) {
}
