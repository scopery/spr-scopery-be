package com.company.scopery.modules.notification.emaildelivery.domain.model;

import com.company.scopery.modules.notification.emaildelivery.domain.enums.EmailDeliveryStatus;

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
