package com.company.scopery.modules.notification.emaildelivery.domain;

import java.util.UUID;

public record EmailDeliverySearchCriteria(
        UUID ruleId,
        UUID templateId,
        UUID eventDefinitionId,
        UUID workspaceId,
        EmailDeliveryStatus status,
        int page,
        int size
) {}
