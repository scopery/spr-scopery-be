package com.company.scopery.common.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSING,
    PUBLISHED,
    FAILED,
    DEAD_LETTER,
    CANCELLED
}
