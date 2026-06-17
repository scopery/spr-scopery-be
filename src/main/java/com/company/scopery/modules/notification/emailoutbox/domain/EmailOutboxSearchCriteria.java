package com.company.scopery.modules.notification.emailoutbox.domain;

import java.util.UUID;

public record EmailOutboxSearchCriteria(
        UUID deliveryId,
        EmailOutboxStatus status,
        EmailProviderType providerType,
        int page,
        int size
) {}
