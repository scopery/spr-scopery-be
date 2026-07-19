package com.company.scopery.modules.resourcecapacity.projectallocation.application.query;

import java.util.UUID;

public record SearchProjectResourceAllocationQuery(
        UUID workspaceId,
        UUID projectId,
        UUID workspaceMemberId,
        UUID userId,
        String status,
        int page,
        int size
) {}
