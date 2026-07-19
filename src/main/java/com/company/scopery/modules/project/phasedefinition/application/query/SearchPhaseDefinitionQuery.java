package com.company.scopery.modules.project.phasedefinition.application.query;

import java.util.UUID;

public record SearchPhaseDefinitionQuery(
        String scope,
        UUID organizationId,
        UUID workspaceId,
        String keyword,
        String status,
        int page,
        int size
) {}
