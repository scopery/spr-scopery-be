package com.company.scopery.modules.aiassistant.domain.enums;

public enum MessageStatus {
    RECEIVED,
    QUEUED,
    CONTEXTUALIZING,
    RETRIEVING,
    GENERATING,
    STREAMING,
    CANCEL_REQUESTED,
    COMPLETED,
    FAILED,
    CANCELLED,
    BLOCKED
}
