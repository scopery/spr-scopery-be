package com.company.scopery.modules.notification.emailrule.domain.enums;

public enum EmailRecipientStrategy {
    EVENT_ACTOR,
    EVENT_TARGET_USER,
    INVITEE_EMAIL,
    REQUESTER_EMAIL,
    STATIC_EMAIL,
    WORKSPACE_OWNER,
    WORKSPACE_USERS_WITH_RIGHT,
    // Phase 20 project strategies
    TASK_ASSIGNEE,
    TASK_IN_CHARGE,
    TASK_WATCHERS,
    PROJECT_WATCHERS,
    PROJECT_OWNER,
    PROJECT_MANAGER,
    CHANGE_REQUEST_REQUESTER,
    CHANGE_WATCHERS,
    QUOTE_WATCHERS,
    FINANCE_WATCHERS,
    BASELINE_WATCHERS,
    EVENT_VARIABLE_USER
}
