package com.company.scopery.modules.notification.emailoutbox.domain.model;

import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailProviderType;

import java.util.UUID;

public record EmailOutboxSearchCriteria(
        UUID deliveryId,
        EmailOutboxStatus status,
        EmailProviderType providerType,
        int page,
        int size
) {}
