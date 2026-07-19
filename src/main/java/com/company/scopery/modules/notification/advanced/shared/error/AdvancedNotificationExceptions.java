package com.company.scopery.modules.notification.advanced.shared.error;

import com.company.scopery.common.exception.AppException;
import java.util.Map;
import java.util.UUID;

public final class AdvancedNotificationExceptions {

    private AdvancedNotificationExceptions() {}

    // ── Preference ────────────────────────────────────────────────────────────

    public static AppException preferenceProfileNotFound(UUID workspaceId, String principalType, UUID principalId) {
        return new AppException(AdvancedNotificationErrorCatalog.PREFERENCE_PROFILE_NOT_FOUND,
                "Notification preference profile not found for principal: " + principalId,
                Map.of("workspaceId", workspaceId, "principalType", principalType, "principalId", principalId));
    }

    public static AppException preferenceAccessDenied() {
        return new AppException(AdvancedNotificationErrorCatalog.PREFERENCE_ACCESS_DENIED);
    }

    public static AppException categoryInvalid(String category) {
        return new AppException(AdvancedNotificationErrorCatalog.NOTIFICATION_CATEGORY_INVALID,
                "Invalid notification category: " + category, Map.of("category", category));
    }

    public static AppException channelNotSupported(String channel) {
        return new AppException(AdvancedNotificationErrorCatalog.CHANNEL_NOT_SUPPORTED,
                "Notification channel not supported: " + channel, Map.of("channel", channel));
    }

    public static AppException channelProviderUnavailable(String channel) {
        return new AppException(AdvancedNotificationErrorCatalog.CHANNEL_PROVIDER_UNAVAILABLE,
                "Provider unavailable for channel: " + channel, Map.of("channel", channel));
    }

    public static AppException quietHoursInvalid(String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.QUIET_HOURS_INVALID,
                "Invalid quiet hours configuration: " + reason, Map.of("reason", reason));
    }

    public static AppException timezoneInvalid(String timezone) {
        return new AppException(AdvancedNotificationErrorCatalog.TIMEZONE_INVALID,
                "Invalid timezone: " + timezone, Map.of("timezone", timezone));
    }

    // ── Subscription ─────────────────────────────────────────────────────────

    public static AppException subscriptionNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.SUBSCRIPTION_NOT_FOUND,
                "Subscription not found: " + id, Map.of("subscriptionId", id));
    }

    public static AppException subscriptionDuplicate(String targetType, UUID targetId) {
        return new AppException(AdvancedNotificationErrorCatalog.SUBSCRIPTION_DUPLICATE,
                "Active subscription already exists for " + targetType + ": " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException subscriptionTargetNotFound(String targetType, UUID targetId) {
        return new AppException(AdvancedNotificationErrorCatalog.SUBSCRIPTION_TARGET_NOT_FOUND,
                "Subscription target not found: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException subscriptionTargetAccessDenied(String targetType, UUID targetId) {
        return new AppException(AdvancedNotificationErrorCatalog.SUBSCRIPTION_TARGET_ACCESS_DENIED,
                "Access denied to subscription target: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    // ── Digest ────────────────────────────────────────────────────────────────

    public static AppException digestRuleNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.DIGEST_RULE_NOT_FOUND,
                "Digest rule not found: " + id, Map.of("digestRuleId", id));
    }

    public static AppException digestRuleInvalid(String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.DIGEST_RULE_INVALID,
                "Invalid digest rule: " + reason, Map.of("reason", reason));
    }

    public static AppException digestRunNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.DIGEST_RUN_NOT_FOUND,
                "Digest run not found: " + id, Map.of("digestRunId", id));
    }

    public static AppException digestRunFailed(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.DIGEST_RUN_FAILED,
                "Digest run failed: " + id, Map.of("digestRunId", id));
    }

    public static AppException digestProviderUnavailable(String channel) {
        return new AppException(AdvancedNotificationErrorCatalog.DIGEST_PROVIDER_UNAVAILABLE,
                "Digest provider unavailable for channel: " + channel, Map.of("channel", channel));
    }

    // ── Reminder ──────────────────────────────────────────────────────────────

    public static AppException reminderRuleNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.REMINDER_RULE_NOT_FOUND,
                "Reminder rule not found: " + id, Map.of("reminderRuleId", id));
    }

    public static AppException reminderRuleInvalid(String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.REMINDER_RULE_INVALID,
                "Invalid reminder rule: " + reason, Map.of("reason", reason));
    }

    public static AppException reminderInstanceNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.REMINDER_INSTANCE_NOT_FOUND,
                "Reminder instance not found: " + id, Map.of("reminderInstanceId", id));
    }

    public static AppException reminderInstanceInvalidStatus(UUID id, String currentStatus) {
        return new AppException(AdvancedNotificationErrorCatalog.REMINDER_INSTANCE_INVALID_STATUS,
                "Reminder instance " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("reminderInstanceId", id, "currentStatus", currentStatus));
    }

    public static AppException reminderSourceNotFound(String sourceType, UUID sourceId) {
        return new AppException(AdvancedNotificationErrorCatalog.REMINDER_SOURCE_NOT_FOUND,
                "Reminder source not found: " + sourceType + " " + sourceId,
                Map.of("sourceType", sourceType, "sourceId", sourceId));
    }

    public static AppException reminderEvaluationFailed(String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.REMINDER_EVALUATION_FAILED,
                "Reminder evaluation failed: " + reason, Map.of("reason", reason));
    }

    // ── Alert ─────────────────────────────────────────────────────────────────

    public static AppException alertRuleNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.ALERT_RULE_NOT_FOUND,
                "Alert rule not found: " + id, Map.of("alertRuleId", id));
    }

    public static AppException alertRuleInvalid(String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.ALERT_RULE_INVALID,
                "Invalid alert rule: " + reason, Map.of("reason", reason));
    }

    public static AppException alertEventNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.ALERT_EVENT_NOT_FOUND,
                "Alert event not found: " + id, Map.of("alertEventId", id));
    }

    public static AppException alertEventInvalidStatus(UUID id, String currentStatus) {
        return new AppException(AdvancedNotificationErrorCatalog.ALERT_EVENT_INVALID_STATUS,
                "Alert event " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("alertEventId", id, "currentStatus", currentStatus));
    }

    public static AppException alertEvaluationFailed(String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.ALERT_EVALUATION_FAILED,
                "Alert evaluation failed: " + reason, Map.of("reason", reason));
    }

    // ── Suppression / dedup ───────────────────────────────────────────────────

    public static AppException suppressionNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.SUPPRESSION_NOT_FOUND,
                "Notification suppression not found: " + id, Map.of("suppressionId", id));
    }

    public static AppException dedupKeyInvalid(String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.DEDUP_KEY_INVALID,
                "Invalid deduplication key: " + reason, Map.of("reason", reason));
    }

    // ── Notification access ───────────────────────────────────────────────────

    public static AppException notificationAccessDenied(UUID notificationId) {
        return new AppException(AdvancedNotificationErrorCatalog.NOTIFICATION_ACCESS_DENIED,
                "Access denied to notification: " + notificationId, Map.of("notificationId", notificationId));
    }

    public static AppException notificationTargetAccessDenied(String targetType, UUID targetId) {
        return new AppException(AdvancedNotificationErrorCatalog.NOTIFICATION_TARGET_ACCESS_DENIED,
                "Access denied to notification target: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException bulkOperationTooLarge(int size, int maxAllowed) {
        return new AppException(AdvancedNotificationErrorCatalog.BULK_OPERATION_TOO_LARGE,
                "Bulk operation size " + size + " exceeds maximum " + maxAllowed,
                Map.of("size", size, "maxAllowed", maxAllowed));
    }

    public static AppException accessDenied() {
        return new AppException(AdvancedNotificationErrorCatalog.ADVANCED_NOTIFICATION_ACCESS_DENIED);
    }

    // ── Delivery ──────────────────────────────────────────────────────────────

    public static AppException deliveryAttemptNotFound(UUID id) {
        return new AppException(AdvancedNotificationErrorCatalog.DELIVERY_ATTEMPT_NOT_FOUND,
                "Delivery attempt not found: " + id, Map.of("deliveryAttemptId", id));
    }

    public static AppException deliveryRetryNotAllowed(UUID id, String reason) {
        return new AppException(AdvancedNotificationErrorCatalog.DELIVERY_RETRY_NOT_ALLOWED,
                "Delivery retry not allowed for attempt " + id + ": " + reason,
                Map.of("deliveryAttemptId", id, "reason", reason));
    }
}
