package com.company.scopery.modules.project.project.application.query;

import java.util.UUID;

public record SearchProjectQuery(
        UUID workspaceId,
        String keyword,
        String status,
        int page,
        int size
) {}
