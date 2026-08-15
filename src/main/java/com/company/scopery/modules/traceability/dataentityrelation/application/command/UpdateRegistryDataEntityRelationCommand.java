package com.company.scopery.modules.traceability.dataentityrelation.application.command;

import com.company.scopery.modules.traceability.dataentityrelation.domain.enums.DataEntityRelationType;

import java.util.UUID;

public record UpdateRegistryDataEntityRelationCommand(
        UUID workspaceId,
        UUID relationId,
        DataEntityRelationType relationType,
        String sourceColumn,
        String label,
        String note) {}
