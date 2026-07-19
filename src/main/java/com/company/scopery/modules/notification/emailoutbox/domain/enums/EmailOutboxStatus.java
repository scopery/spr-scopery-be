package com.company.scopery.modules.notification.emailoutbox.domain.enums;

public enum EmailOutboxStatus {
    PENDING,
    RETRY_SCHEDULED,
    PROCESSING,
    SENT,
    FAILED,
    DEAD_LETTER,
    CANCELLED,
    SKIPPED
}
