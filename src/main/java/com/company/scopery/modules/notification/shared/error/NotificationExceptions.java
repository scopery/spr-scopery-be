package com.company.scopery.modules.notification.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class NotificationExceptions {

    private NotificationExceptions() {}

    // ── Email Template ────────────────────────────────────────────────────────

    public static AppException emailTemplateNotFound(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_NOT_FOUND,
                "Email template not found: " + id, Map.of("id", id));
    }

    public static AppException emailTemplateCodeAlreadyExists(String code) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_CODE_ALREADY_EXISTS,
                "Email template code already exists: " + code, Map.of("code", code));
    }

    public static AppException emailTemplateNotActive(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_NOT_ACTIVE,
                "Email template is not active: " + id, Map.of("id", id));
    }

    public static AppException emailTemplateDeleted(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_DELETED,
                "Email template has been deleted: " + id, Map.of("id", id));
    }

    public static AppException emailTemplateEventDefinitionNotFound(UUID eventDefinitionId) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_EVENT_DEFINITION_NOT_FOUND,
                "Event definition not found: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }

    public static AppException emailTemplateEventDefinitionNotActive(UUID eventDefinitionId) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_EVENT_DEFINITION_NOT_ACTIVE,
                "Linked event definition is not active: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }

    // ── Email Template Version ────────────────────────────────────────────────

    public static AppException emailTemplateVersionNotFound(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_VERSION_NOT_FOUND,
                "Email template version not found: " + id, Map.of("id", id));
    }

    public static AppException emailTemplateVersionNotPublished(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_VERSION_NOT_PUBLISHED,
                "Email template version is not published: " + id, Map.of("id", id));
    }

    public static AppException emailTemplateVersionVariableMissing(String variablePath) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_VERSION_VARIABLE_MISSING,
                "Template uses undefined variable: {{" + variablePath + "}}",
                Map.of("variablePath", variablePath));
    }

    public static AppException emailTemplateSensitiveVariableNotAllowed(String variablePath, String location) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_SENSITIVE_VARIABLE_NOT_ALLOWED,
                "Sensitive variable {{" + variablePath + "}} is not allowed in " + location,
                Map.of("variablePath", variablePath, "location", location));
    }

    public static AppException emailTemplateRenderFailed(String reason) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_RENDER_FAILED,
                "Template rendering failed: " + reason, Map.of("reason", reason));
    }

    public static AppException emailTemplateNoPublishedVersion(UUID templateId) {
        return new AppException(NotificationErrorCatalog.EMAIL_TEMPLATE_NO_PUBLISHED_VERSION,
                "Email template has no published version: " + templateId,
                Map.of("templateId", templateId));
    }

    // ── Email Rule ────────────────────────────────────────────────────────────

    public static AppException emailRuleNotFound(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_NOT_FOUND,
                "Email rule not found: " + id, Map.of("id", id));
    }

    public static AppException emailRuleCodeAlreadyExists(String code) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_CODE_ALREADY_EXISTS,
                "Email rule code already exists: " + code, Map.of("code", code));
    }

    public static AppException emailRuleNotActive(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_NOT_ACTIVE,
                "Email rule is not active: " + id, Map.of("id", id));
    }

    public static AppException emailRuleTemplateNotFound(UUID templateId) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_TEMPLATE_NOT_FOUND,
                "Email template not found for rule: " + templateId,
                Map.of("templateId", templateId));
    }

    public static AppException emailRuleTemplateNotActive(UUID templateId) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_TEMPLATE_NOT_ACTIVE,
                "Linked email template is not active: " + templateId,
                Map.of("templateId", templateId));
    }

    public static AppException emailRuleEventDefinitionNotFound(UUID eventDefinitionId) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_EVENT_DEFINITION_NOT_FOUND,
                "Event definition not found for rule: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }

    public static AppException emailRuleEventDefinitionNotActive(UUID eventDefinitionId) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_EVENT_DEFINITION_NOT_ACTIVE,
                "Linked event definition is not active: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }

    public static AppException emailRuleSensitiveNotPermitted(UUID ruleId) {
        return new AppException(NotificationErrorCatalog.EMAIL_RULE_SENSITIVE_NOT_PERMITTED,
                "Email rule is not permitted to use sensitive variables: " + ruleId,
                Map.of("ruleId", ruleId));
    }

    // ── Email Delivery ────────────────────────────────────────────────────────

    public static AppException emailDeliveryNotFound(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_DELIVERY_NOT_FOUND,
                "Email delivery not found: " + id, Map.of("id", id));
    }

    // ── Email Outbox ──────────────────────────────────────────────────────────

    public static AppException emailOutboxNotFound(UUID id) {
        return new AppException(NotificationErrorCatalog.EMAIL_OUTBOX_NOT_FOUND,
                "Email outbox record not found: " + id, Map.of("id", id));
    }

    public static AppException emailOutboxMaxRetryReached(UUID id, int maxRetry) {
        return new AppException(NotificationErrorCatalog.EMAIL_OUTBOX_MAX_RETRY_REACHED,
                "Email outbox record has reached the maximum retry limit of " + maxRetry + ": " + id,
                Map.of("id", id, "maxRetry", maxRetry));
    }

    public static AppException emailOutboxNotRetryable(UUID id, String status) {
        return new AppException(NotificationErrorCatalog.EMAIL_OUTBOX_NOT_RETRYABLE,
                "Email outbox record is not in a retryable state (status=" + status + "): " + id,
                Map.of("id", id, "status", status));
    }

    public static AppException emailOutboxNotCancellable(UUID id, String status) {
        return new AppException(NotificationErrorCatalog.EMAIL_OUTBOX_NOT_CANCELLABLE,
                "Email outbox record cannot be cancelled (status=" + status + "): " + id,
                Map.of("id", id, "status", status));
    }

    // ── Notification Item ─────────────────────────────────────────────────────

    public static AppException notificationItemNotFound(UUID id) {
        return new AppException(NotificationErrorCatalog.NOTIFICATION_ITEM_NOT_FOUND,
                "Notification not found: " + id, Map.of("id", id));
    }

    public static AppException notificationItemAccessDenied(UUID id) {
        return new AppException(NotificationErrorCatalog.NOTIFICATION_ITEM_ACCESS_DENIED,
                "Access denied for notification: " + id, Map.of("id", id));
    }

    // ── Dispatch ──────────────────────────────────────────────────────────────

    public static AppException dispatchEventDefinitionNotFound(UUID eventDefinitionId) {
        return new AppException(NotificationErrorCatalog.EMAIL_DISPATCH_EVENT_DEFINITION_NOT_FOUND,
                "Event definition not found for dispatch: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }

    public static AppException dispatchEventDefinitionNotActive(UUID eventDefinitionId) {
        return new AppException(NotificationErrorCatalog.EMAIL_DISPATCH_EVENT_DEFINITION_NOT_ACTIVE,
                "Event definition is not active: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }
}
