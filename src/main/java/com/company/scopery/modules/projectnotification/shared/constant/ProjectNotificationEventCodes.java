package com.company.scopery.modules.projectnotification.shared.constant;

import java.util.Set;

public final class ProjectNotificationEventCodes {
    public static final String PROJECT_TASK_DUE_SOON = "PROJECT_TASK_DUE_SOON";
    public static final String PROJECT_TASK_OVERDUE = "PROJECT_TASK_OVERDUE";
    public static final String PROJECT_TASK_AT_RISK = "PROJECT_TASK_AT_RISK";

    /** Outbox event codes that should fan into Phase 06 notification dispatch. */
    public static final Set<String> BRIDGEABLE_EVENT_CODES = Set.of(
            "TASK_ASSIGNED",
            "TASK_DUE_DATE_CAPACITY_GAP_DETECTED",
            "SCHEDULE_RUN_FAILED",
            "TASK_SCHEDULE_AT_RISK",
            "RESOURCE_SCHEDULE_OVER_CAPACITY_DETECTED",
            "PROJECT_BASELINE_APPROVED",
            "CHANGE_REQUEST_SUBMITTED",
            "CHANGE_REQUEST_APPROVED",
            "CHANGE_REQUEST_REJECTED",
            "CHANGE_REQUEST_APPLIED",
            "CHANGE_REQUEST_APPLY_FAILED",
            "CHANGE_ORDER_APPROVED",
            "POST_BASELINE_EDIT_BLOCKED",
            "QUOTE_SUBMITTED",
            "QUOTE_APPROVED",
            "QUOTE_REJECTED",
            "QUOTE_SENT",
            "QUOTE_ACCEPTED",
            "PROJECT_FINANCE_SCENARIO_APPROVED",
            "PROJECT_MARGIN_THRESHOLD_WARNING",
            PROJECT_TASK_DUE_SOON,
            PROJECT_TASK_OVERDUE,
            PROJECT_TASK_AT_RISK
    );

    private ProjectNotificationEventCodes() {}
}
