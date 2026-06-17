package com.company.scopery.modules.notification.emailoutbox.application.query;

import java.util.UUID;

public record SearchEmailOutboxQuery(
        UUID deliveryId,
        String status,
        String providerType,
        int page,
        int size
) {}
