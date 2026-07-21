package com.company.scopery.modules.aiaction.execution.domain.enums;

public enum AiActionExecutionStatus {
    QUEUED,
    RUNNING,
    PAUSING,
    PAUSED,
    RESUMING,
    CANCEL_REQUESTED,
    CANCELLED,
    SUCCEEDED,
    PARTIAL,
    FAILED,
    COMPENSATING,
    COMPENSATED,
    COMPENSATION_PARTIAL,
    COMPENSATION_FAILED
}
