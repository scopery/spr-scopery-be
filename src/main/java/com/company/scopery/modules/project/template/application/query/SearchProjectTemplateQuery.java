package com.company.scopery.modules.project.template.application.query;

import java.util.UUID;

public record SearchProjectTemplateQuery(
        String scope,
        UUID workspaceId,
        UUID organizationId,
        String status,
        String category,
        String keyword,
        int page,
        int size
) {}
