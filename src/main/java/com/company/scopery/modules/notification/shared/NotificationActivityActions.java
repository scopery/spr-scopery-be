package com.company.scopery.modules.notification.shared;

public final class NotificationActivityActions {
    public static final String CREATE_EMAIL_TEMPLATE          = "CREATE_EMAIL_TEMPLATE";
    public static final String UPDATE_EMAIL_TEMPLATE          = "UPDATE_EMAIL_TEMPLATE";
    public static final String CREATE_EMAIL_TEMPLATE_VERSION  = "CREATE_EMAIL_TEMPLATE_VERSION";
    public static final String PUBLISH_EMAIL_TEMPLATE_VERSION = "PUBLISH_EMAIL_TEMPLATE_VERSION";
    public static final String ACTIVATE_EMAIL_TEMPLATE        = "ACTIVATE_EMAIL_TEMPLATE";
    public static final String DEACTIVATE_EMAIL_TEMPLATE      = "DEACTIVATE_EMAIL_TEMPLATE";
    public static final String SOFT_DELETE_EMAIL_TEMPLATE     = "SOFT_DELETE_EMAIL_TEMPLATE";
    public static final String CREATE_EMAIL_RULE              = "CREATE_EMAIL_RULE";
    public static final String UPDATE_EMAIL_RULE              = "UPDATE_EMAIL_RULE";
    public static final String ACTIVATE_EMAIL_RULE            = "ACTIVATE_EMAIL_RULE";
    public static final String DEACTIVATE_EMAIL_RULE          = "DEACTIVATE_EMAIL_RULE";
    public static final String SOFT_DELETE_EMAIL_RULE         = "SOFT_DELETE_EMAIL_RULE";
    public static final String MARK_EMAIL_RULE_MANDATORY      = "MARK_EMAIL_RULE_MANDATORY";
    public static final String ALLOW_EMAIL_RULE_SENSITIVE_VARIABLES = "ALLOW_EMAIL_RULE_SENSITIVE_VARIABLES";
    public static final String CREATE_EMAIL_DELIVERY          = "CREATE_EMAIL_DELIVERY";
    public static final String ENQUEUE_EMAIL                  = "ENQUEUE_EMAIL";
    public static final String SEND_EMAIL                     = "SEND_EMAIL";
    public static final String FAIL_EMAIL                     = "FAIL_EMAIL";
    public static final String RETRY_EMAIL                    = "RETRY_EMAIL";
    public static final String CANCEL_EMAIL                   = "CANCEL_EMAIL";
    public static final String EMAIL_DELIVERY_DEAD_LETTERED   = "EMAIL_DELIVERY_DEAD_LETTERED";
    public static final String NOTIFICATION_DEDUPLICATED      = "NOTIFICATION_DEDUPLICATED";
    public static final String NOTIFICATION_SENSITIVE_VARIABLE_MASKED = "NOTIFICATION_SENSITIVE_VARIABLE_MASKED";
    public static final String NOTIFICATION_ITEM_CREATED      = "NOTIFICATION_ITEM_CREATED";
    public static final String MARK_NOTIFICATION_READ         = "MARK_NOTIFICATION_READ";
    public static final String MARK_ALL_NOTIFICATIONS_READ    = "MARK_ALL_NOTIFICATIONS_READ";
    public static final String DISMISS_NOTIFICATION           = "DISMISS_NOTIFICATION";

    private NotificationActivityActions() {}
}
