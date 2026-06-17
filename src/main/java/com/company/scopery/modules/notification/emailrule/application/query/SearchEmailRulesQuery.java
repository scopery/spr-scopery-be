package com.company.scopery.modules.notification.emailrule.application.query;

import java.util.UUID;

public record SearchEmailRulesQuery(
        String keyword,
        String scope,
        String status,
        UUID workspaceId,
        UUID eventDefinitionId,
        UUID templateId,
        int page,
        int size
) {}
