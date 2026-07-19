package com.company.scopery.modules.projectnotification.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ProjectNotificationErrorCatalog implements ErrorCatalog {
    PROJECT_NOTIFICATION_SUBSCRIPTION_NOT_FOUND("PROJECT_NOTIFICATION_SUBSCRIPTION_NOT_FOUND", "Project notification subscription not found", HttpStatus.NOT_FOUND),
    PROJECT_NOTIFICATION_SUBSCRIPTION_DUPLICATE("PROJECT_NOTIFICATION_SUBSCRIPTION_DUPLICATE", "Active project notification subscription already exists", HttpStatus.CONFLICT),
    PROJECT_NOTIFICATION_SUBSCRIBER_NOT_MEMBER("PROJECT_NOTIFICATION_SUBSCRIBER_NOT_MEMBER", "Subscriber is not a workspace member", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOTIFICATION_SUBSCRIBER_INACTIVE("PROJECT_NOTIFICATION_SUBSCRIBER_INACTIVE", "Subscriber workspace membership is inactive", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOTIFICATION_SUBSCRIBER_NO_ACCESS("PROJECT_NOTIFICATION_SUBSCRIBER_NO_ACCESS", "Subscriber does not have project access", HttpStatus.FORBIDDEN),
    PROJECT_NOTIFICATION_SUBSCRIPTION_MANDATORY("PROJECT_NOTIFICATION_SUBSCRIPTION_MANDATORY", "Mandatory subscription cannot be deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOTIFICATION_SUBSCRIPTION_ACCESS_DENIED("PROJECT_NOTIFICATION_SUBSCRIPTION_ACCESS_DENIED", "Project notification subscription access denied", HttpStatus.FORBIDDEN),

    TASK_NOTIFICATION_SUBSCRIPTION_NOT_FOUND("TASK_NOTIFICATION_SUBSCRIPTION_NOT_FOUND", "Task notification subscription not found", HttpStatus.NOT_FOUND),
    TASK_NOTIFICATION_SUBSCRIPTION_DUPLICATE("TASK_NOTIFICATION_SUBSCRIPTION_DUPLICATE", "Active task notification subscription already exists", HttpStatus.CONFLICT),
    TASK_NOTIFICATION_TASK_MISMATCH("TASK_NOTIFICATION_TASK_MISMATCH", "Task does not belong to project", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_NOTIFICATION_SUBSCRIBER_NO_ACCESS("TASK_NOTIFICATION_SUBSCRIBER_NO_ACCESS", "Subscriber does not have task access", HttpStatus.FORBIDDEN),

    PROJECT_NOTIFICATION_PREFERENCE_NOT_FOUND("PROJECT_NOTIFICATION_PREFERENCE_NOT_FOUND", "Project notification preference not found", HttpStatus.NOT_FOUND),
    PROJECT_NOTIFICATION_PREFERENCE_INVALID_CHANNEL("PROJECT_NOTIFICATION_PREFERENCE_INVALID_CHANNEL", "Invalid notification channel", HttpStatus.BAD_REQUEST),
    PROJECT_NOTIFICATION_PREFERENCE_INVALID_EVENT("PROJECT_NOTIFICATION_PREFERENCE_INVALID_EVENT", "Invalid notification event code", HttpStatus.BAD_REQUEST),
    PROJECT_NOTIFICATION_PREFERENCE_ACCESS_DENIED("PROJECT_NOTIFICATION_PREFERENCE_ACCESS_DENIED", "Notification preference access denied", HttpStatus.FORBIDDEN),

    PROJECT_NOTIFICATION_RULE_EVENT_NOT_ACTIVE("PROJECT_NOTIFICATION_RULE_EVENT_NOT_ACTIVE", "Notification rule event is not active", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOTIFICATION_RULE_TEMPLATE_NOT_PUBLISHED("PROJECT_NOTIFICATION_RULE_TEMPLATE_NOT_PUBLISHED", "Notification template is not published", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOTIFICATION_RULE_RECIPIENT_STRATEGY_INVALID("PROJECT_NOTIFICATION_RULE_RECIPIENT_STRATEGY_INVALID", "Invalid recipient strategy", HttpStatus.BAD_REQUEST),
    PROJECT_NOTIFICATION_RULE_DEDUP_STRATEGY_MISSING("PROJECT_NOTIFICATION_RULE_DEDUP_STRATEGY_MISSING", "Dedup strategy is missing", HttpStatus.BAD_REQUEST),

    PROJECT_NOTIFICATION_RECIPIENT_RESOLUTION_FAILED("PROJECT_NOTIFICATION_RECIPIENT_RESOLUTION_FAILED", "Recipient resolution failed", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_NOTIFICATION_PAYLOAD_MASKING_FAILED("PROJECT_NOTIFICATION_PAYLOAD_MASKING_FAILED", "Payload masking failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROJECT_NOTIFICATION_DEDUPLICATED("PROJECT_NOTIFICATION_DEDUPLICATED", "Notification was deduplicated", HttpStatus.CONFLICT),

    PROJECT_REMINDER_RUN_NOT_FOUND("PROJECT_REMINDER_RUN_NOT_FOUND", "Reminder run not found", HttpStatus.NOT_FOUND),
    PROJECT_REMINDER_RUN_ALREADY_RUNNING("PROJECT_REMINDER_RUN_ALREADY_RUNNING", "A reminder run is already running", HttpStatus.CONFLICT),
    PROJECT_REMINDER_RUN_FAILED("PROJECT_REMINDER_RUN_FAILED", "Reminder run failed", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_REMINDER_INVALID_POLICY("PROJECT_REMINDER_INVALID_POLICY", "Invalid reminder policy", HttpStatus.BAD_REQUEST),

    PROJECT_NOTIFICATION_ACCESS_DENIED("PROJECT_NOTIFICATION_ACCESS_DENIED", "Project notification access denied", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProjectNotificationErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
