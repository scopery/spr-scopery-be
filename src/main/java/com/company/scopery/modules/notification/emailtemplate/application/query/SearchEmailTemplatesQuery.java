package com.company.scopery.modules.notification.emailtemplate.application.query;

import java.util.UUID;

public record SearchEmailTemplatesQuery(
        String keyword,
        String scope,
        String status,
        UUID workspaceId,
        UUID eventDefinitionId,
        int page,
        int size
) {}
