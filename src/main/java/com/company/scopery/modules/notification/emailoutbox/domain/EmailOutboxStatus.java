package com.company.scopery.modules.notification.emailoutbox.domain;

public enum EmailOutboxStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED,
    CANCELLED,
    SKIPPED
}
