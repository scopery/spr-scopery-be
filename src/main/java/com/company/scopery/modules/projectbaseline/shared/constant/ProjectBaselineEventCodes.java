package com.company.scopery.modules.projectbaseline.shared.constant;

public final class ProjectBaselineEventCodes {
    public static final String PROJECT_BASELINE_CREATED = "PROJECT_BASELINE_CREATED";
    public static final String PROJECT_BASELINE_REFRESHED = "PROJECT_BASELINE_REFRESHED";
    public static final String PROJECT_BASELINE_VALIDATED = "PROJECT_BASELINE_VALIDATED";
    public static final String PROJECT_BASELINE_APPROVED = "PROJECT_BASELINE_APPROVED";
    public static final String PROJECT_BASELINE_MARKED_CURRENT = "PROJECT_BASELINE_MARKED_CURRENT";
    public static final String PROJECT_BASELINE_ARCHIVED = "PROJECT_BASELINE_ARCHIVED";

    public static final String CHANGE_REQUEST_CREATED = "CHANGE_REQUEST_CREATED";
    public static final String CHANGE_REQUEST_UPDATED = "CHANGE_REQUEST_UPDATED";
    public static final String CHANGE_REQUEST_ITEM_CREATED = "CHANGE_REQUEST_ITEM_CREATED";
    public static final String CHANGE_REQUEST_ITEM_UPDATED = "CHANGE_REQUEST_ITEM_UPDATED";
    public static final String CHANGE_REQUEST_ITEM_DELETED = "CHANGE_REQUEST_ITEM_DELETED";
    public static final String CHANGE_IMPACT_UPDATED = "CHANGE_IMPACT_UPDATED";
    public static final String CHANGE_IMPACT_CALCULATED = "CHANGE_IMPACT_CALCULATED";
    public static final String CHANGE_REQUEST_SUBMITTED = "CHANGE_REQUEST_SUBMITTED";
    public static final String CHANGE_REQUEST_APPROVED = "CHANGE_REQUEST_APPROVED";
    public static final String CHANGE_REQUEST_REJECTED = "CHANGE_REQUEST_REJECTED";
    public static final String CHANGE_REQUEST_CANCELLED = "CHANGE_REQUEST_CANCELLED";
    public static final String CHANGE_REQUEST_APPLIED = "CHANGE_REQUEST_APPLIED";
    public static final String CHANGE_REQUEST_APPLY_FAILED = "CHANGE_REQUEST_APPLY_FAILED";
    public static final String CHANGE_REQUEST_ARCHIVED = "CHANGE_REQUEST_ARCHIVED";

    public static final String CHANGE_ORDER_CREATED = "CHANGE_ORDER_CREATED";
    public static final String CHANGE_ORDER_UPDATED = "CHANGE_ORDER_UPDATED";
    public static final String CHANGE_ORDER_SUBMITTED = "CHANGE_ORDER_SUBMITTED";
    public static final String CHANGE_ORDER_APPROVED = "CHANGE_ORDER_APPROVED";
    public static final String CHANGE_ORDER_REJECTED = "CHANGE_ORDER_REJECTED";
    public static final String CHANGE_ORDER_ARCHIVED = "CHANGE_ORDER_ARCHIVED";

    public static final String POST_BASELINE_EDIT_BLOCKED = "POST_BASELINE_EDIT_BLOCKED";
    public static final String BASELINE_OVERRIDE_EDIT_USED = "BASELINE_OVERRIDE_EDIT_USED";

    private ProjectBaselineEventCodes() {}
}
