package com.company.scopery.modules.notification.advanced.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AdvancedNotificationErrorCatalog implements ErrorCatalog {

    // Preference
    PREFERENCE_PROFILE_NOT_FOUND("NOTIFICATION_PREFERENCE_PROFILE_NOT_FOUND", "Notification preference profile not found", HttpStatus.NOT_FOUND),
    PREFERENCE_ACCESS_DENIED("NOTIFICATION_PREFERENCE_ACCESS_DENIED", "Access denied to notification preference", HttpStatus.FORBIDDEN),
    NOTIFICATION_CATEGORY_INVALID("NOTIFICATION_CATEGORY_INVALID", "Invalid notification category", HttpStatus.BAD_REQUEST),
    CHANNEL_NOT_SUPPORTED("NOTIFICATION_CHANNEL_NOT_SUPPORTED", "Notification channel not supported", HttpStatus.BAD_REQUEST),
    CHANNEL_PROVIDER_UNAVAILABLE("NOTIFICATION_CHANNEL_PROVIDER_UNAVAILABLE", "Notification channel provider is unavailable", HttpStatus.UNPROCESSABLE_ENTITY),
    QUIET_HOURS_INVALID("QUIET_HOURS_INVALID", "Invalid quiet hours configuration", HttpStatus.BAD_REQUEST),
    TIMEZONE_INVALID("TIMEZONE_INVALID", "Invalid or unrecognised timezone", HttpStatus.BAD_REQUEST),

    // Subscription
    SUBSCRIPTION_NOT_FOUND("NOTIFICATION_SUBSCRIPTION_NOT_FOUND", "Subscription not found", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_DUPLICATE("NOTIFICATION_SUBSCRIPTION_DUPLICATE", "Active subscription already exists for this target", HttpStatus.CONFLICT),
    SUBSCRIPTION_TARGET_NOT_FOUND("NOTIFICATION_SUBSCRIPTION_TARGET_NOT_FOUND", "Subscription target not found", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_TARGET_ACCESS_DENIED("NOTIFICATION_SUBSCRIPTION_TARGET_ACCESS_DENIED", "Access denied to subscription target", HttpStatus.FORBIDDEN),

    // Digest
    DIGEST_RULE_NOT_FOUND("NOTIFICATION_DIGEST_RULE_NOT_FOUND", "Digest rule not found", HttpStatus.NOT_FOUND),
    DIGEST_RULE_INVALID("DIGEST_RULE_INVALID", "Invalid digest rule configuration", HttpStatus.BAD_REQUEST),
    DIGEST_RUN_NOT_FOUND("NOTIFICATION_DIGEST_RUN_NOT_FOUND", "Digest run not found", HttpStatus.NOT_FOUND),
    DIGEST_RUN_FAILED("DIGEST_RUN_FAILED", "Digest run failed", HttpStatus.UNPROCESSABLE_ENTITY),
    DIGEST_PROVIDER_UNAVAILABLE("DIGEST_PROVIDER_UNAVAILABLE", "Digest delivery provider is unavailable", HttpStatus.UNPROCESSABLE_ENTITY),

    // Reminder
    REMINDER_RULE_NOT_FOUND("NOTIFICATION_REMINDER_RULE_NOT_FOUND", "Reminder rule not found", HttpStatus.NOT_FOUND),
    REMINDER_RULE_INVALID("REMINDER_RULE_INVALID", "Invalid reminder rule configuration", HttpStatus.BAD_REQUEST),
    REMINDER_INSTANCE_NOT_FOUND("NOTIFICATION_REMINDER_INSTANCE_NOT_FOUND", "Reminder instance not found", HttpStatus.NOT_FOUND),
    REMINDER_INSTANCE_INVALID_STATUS("REMINDER_INSTANCE_INVALID_STATUS", "Reminder instance status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),
    REMINDER_SOURCE_NOT_FOUND("REMINDER_SOURCE_NOT_FOUND", "Reminder source object not found", HttpStatus.NOT_FOUND),
    REMINDER_EVALUATION_FAILED("REMINDER_EVALUATION_FAILED", "Reminder evaluation failed", HttpStatus.UNPROCESSABLE_ENTITY),

    // Alert
    ALERT_RULE_NOT_FOUND("NOTIFICATION_ALERT_RULE_NOT_FOUND", "Alert rule not found", HttpStatus.NOT_FOUND),
    ALERT_RULE_INVALID("ALERT_RULE_INVALID", "Invalid alert rule configuration", HttpStatus.BAD_REQUEST),
    ALERT_EVENT_NOT_FOUND("NOTIFICATION_ALERT_EVENT_NOT_FOUND", "Alert event not found", HttpStatus.NOT_FOUND),
    ALERT_EVENT_INVALID_STATUS("ALERT_EVENT_INVALID_STATUS", "Alert event status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),
    ALERT_EVALUATION_FAILED("ALERT_EVALUATION_FAILED", "Alert evaluation failed", HttpStatus.UNPROCESSABLE_ENTITY),

    // Suppression / dedup
    SUPPRESSION_NOT_FOUND("NOTIFICATION_SUPPRESSION_NOT_FOUND", "Notification suppression record not found", HttpStatus.NOT_FOUND),
    DEDUP_KEY_INVALID("NOTIFICATION_DEDUP_KEY_INVALID", "Invalid notification deduplication key", HttpStatus.BAD_REQUEST),

    // Notification access
    NOTIFICATION_ACCESS_DENIED("NOTIFICATION_ACCESS_DENIED", "Access denied to notification", HttpStatus.FORBIDDEN),
    NOTIFICATION_TARGET_ACCESS_DENIED("NOTIFICATION_TARGET_ACCESS_DENIED", "Access denied to notification target", HttpStatus.FORBIDDEN),
    BULK_OPERATION_TOO_LARGE("NOTIFICATION_BULK_OPERATION_TOO_LARGE", "Bulk notification operation exceeds maximum allowed size", HttpStatus.BAD_REQUEST),
    ADVANCED_NOTIFICATION_ACCESS_DENIED("ADVANCED_NOTIFICATION_ACCESS_DENIED", "Advanced notification access denied", HttpStatus.FORBIDDEN),

    // Delivery
    DELIVERY_ATTEMPT_NOT_FOUND("DELIVERY_ATTEMPT_NOT_FOUND", "Delivery attempt not found", HttpStatus.NOT_FOUND),
    DELIVERY_RETRY_NOT_ALLOWED("DELIVERY_RETRY_NOT_ALLOWED", "Delivery retry is not allowed for this attempt", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AdvancedNotificationErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
