package com.company.scopery.modules.notification.emaildelivery.application.query;

import java.util.UUID;

public record SearchEmailDeliveriesQuery(
        UUID ruleId,
        UUID templateId,
        UUID eventDefinitionId,
        UUID workspaceId,
        String status,
        int page,
        int size
) {}
