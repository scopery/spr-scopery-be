package com.company.scopery.modules.aiagent.execution.domain.enums;

public enum ExecutionStatus {
    PENDING,
    RUNNING,
    SUCCEEDED,
    FAILED,
    CANCELLED,
    BLOCKED;

    public boolean isTerminal() {
        return this == SUCCEEDED || this == FAILED || this == CANCELLED || this == BLOCKED;
    }
}
