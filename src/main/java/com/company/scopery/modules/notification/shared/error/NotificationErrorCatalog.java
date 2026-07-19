package com.company.scopery.modules.notification.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum NotificationErrorCatalog implements ErrorCatalog {

    // ── Email Template ────────────────────────────────────────────────────────

    EMAIL_TEMPLATE_NOT_FOUND(
            "EMAIL_TEMPLATE_NOT_FOUND",
            "Email template not found",
            HttpStatus.NOT_FOUND),

    EMAIL_TEMPLATE_CODE_ALREADY_EXISTS(
            "EMAIL_TEMPLATE_CODE_ALREADY_EXISTS",
            "Email template code already exists",
            HttpStatus.CONFLICT),

    EMAIL_TEMPLATE_SCOPE_INVALID(
            "EMAIL_TEMPLATE_SCOPE_INVALID",
            "Invalid email template scope",
            HttpStatus.BAD_REQUEST),

    INVALID_EMAIL_TEMPLATE_STATUS(
            "INVALID_EMAIL_TEMPLATE_STATUS",
            "Invalid email template status",
            HttpStatus.BAD_REQUEST),

    EMAIL_TEMPLATE_NOT_ACTIVE(
            "EMAIL_TEMPLATE_NOT_ACTIVE",
            "Email template is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_TEMPLATE_DELETED(
            "EMAIL_TEMPLATE_DELETED",
            "Email template has been deleted",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_TEMPLATE_EVENT_DEFINITION_NOT_FOUND(
            "EMAIL_TEMPLATE_EVENT_DEFINITION_NOT_FOUND",
            "Event definition not found for this email template",
            HttpStatus.NOT_FOUND),

    EMAIL_TEMPLATE_EVENT_DEFINITION_NOT_ACTIVE(
            "EMAIL_TEMPLATE_EVENT_DEFINITION_NOT_ACTIVE",
            "Linked event definition is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Email Template Version ────────────────────────────────────────────────

    EMAIL_TEMPLATE_VERSION_NOT_FOUND(
            "EMAIL_TEMPLATE_VERSION_NOT_FOUND",
            "Email template version not found",
            HttpStatus.NOT_FOUND),

    INVALID_EMAIL_TEMPLATE_VERSION_STATUS(
            "INVALID_EMAIL_TEMPLATE_VERSION_STATUS",
            "Invalid email template version status",
            HttpStatus.BAD_REQUEST),

    EMAIL_TEMPLATE_VERSION_NOT_PUBLISHED(
            "EMAIL_TEMPLATE_VERSION_NOT_PUBLISHED",
            "Email template version is not published",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_TEMPLATE_VERSION_VARIABLE_MISSING(
            "EMAIL_TEMPLATE_VERSION_VARIABLE_MISSING",
            "Template uses variables not defined in the event registry",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_TEMPLATE_SENSITIVE_VARIABLE_NOT_ALLOWED(
            "EMAIL_TEMPLATE_SENSITIVE_VARIABLE_NOT_ALLOWED",
            "Sensitive template variables are not allowed in this context",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_TEMPLATE_RENDER_FAILED(
            "EMAIL_TEMPLATE_RENDER_FAILED",
            "Email template rendering failed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_TEMPLATE_NO_PUBLISHED_VERSION(
            "EMAIL_TEMPLATE_NO_PUBLISHED_VERSION",
            "Email template has no published version",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Email Rule ────────────────────────────────────────────────────────────

    EMAIL_RULE_NOT_FOUND(
            "EMAIL_RULE_NOT_FOUND",
            "Email rule not found",
            HttpStatus.NOT_FOUND),

    EMAIL_RULE_CODE_ALREADY_EXISTS(
            "EMAIL_RULE_CODE_ALREADY_EXISTS",
            "Email rule code already exists",
            HttpStatus.CONFLICT),

    INVALID_EMAIL_RULE_SCOPE(
            "INVALID_EMAIL_RULE_SCOPE",
            "Invalid email rule scope",
            HttpStatus.BAD_REQUEST),

    INVALID_EMAIL_RULE_STATUS(
            "INVALID_EMAIL_RULE_STATUS",
            "Invalid email rule status",
            HttpStatus.BAD_REQUEST),

    EMAIL_RULE_NOT_ACTIVE(
            "EMAIL_RULE_NOT_ACTIVE",
            "Email rule is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_RULE_TEMPLATE_NOT_FOUND(
            "EMAIL_RULE_TEMPLATE_NOT_FOUND",
            "Email template not found for this rule",
            HttpStatus.NOT_FOUND),

    EMAIL_RULE_TEMPLATE_NOT_ACTIVE(
            "EMAIL_RULE_TEMPLATE_NOT_ACTIVE",
            "Linked email template is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_RULE_EVENT_DEFINITION_NOT_FOUND(
            "EMAIL_RULE_EVENT_DEFINITION_NOT_FOUND",
            "Event definition not found for this rule",
            HttpStatus.NOT_FOUND),

    EMAIL_RULE_EVENT_DEFINITION_NOT_ACTIVE(
            "EMAIL_RULE_EVENT_DEFINITION_NOT_ACTIVE",
            "Linked event definition is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_EMAIL_RECIPIENT_STRATEGY(
            "INVALID_EMAIL_RECIPIENT_STRATEGY",
            "Invalid email recipient strategy",
            HttpStatus.BAD_REQUEST),

    EMAIL_RULE_SENSITIVE_NOT_PERMITTED(
            "EMAIL_RULE_SENSITIVE_NOT_PERMITTED",
            "Email rule is not permitted to use sensitive variables",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Email Delivery ────────────────────────────────────────────────────────

    EMAIL_DELIVERY_NOT_FOUND(
            "EMAIL_DELIVERY_NOT_FOUND",
            "Email delivery not found",
            HttpStatus.NOT_FOUND),

    INVALID_EMAIL_DELIVERY_STATUS(
            "INVALID_EMAIL_DELIVERY_STATUS",
            "Invalid email delivery status",
            HttpStatus.BAD_REQUEST),

    // ── Email Outbox ──────────────────────────────────────────────────────────

    EMAIL_OUTBOX_NOT_FOUND(
            "EMAIL_OUTBOX_NOT_FOUND",
            "Email outbox record not found",
            HttpStatus.NOT_FOUND),

    INVALID_EMAIL_OUTBOX_STATUS(
            "INVALID_EMAIL_OUTBOX_STATUS",
            "Invalid email outbox status",
            HttpStatus.BAD_REQUEST),

    INVALID_EMAIL_PROVIDER_TYPE(
            "INVALID_EMAIL_PROVIDER_TYPE",
            "Invalid email provider type",
            HttpStatus.BAD_REQUEST),

    EMAIL_OUTBOX_MAX_RETRY_REACHED(
            "EMAIL_OUTBOX_MAX_RETRY_REACHED",
            "Email outbox record has reached the maximum retry limit",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_OUTBOX_NOT_RETRYABLE(
            "EMAIL_OUTBOX_NOT_RETRYABLE",
            "Email outbox record is not in a retryable state",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EMAIL_OUTBOX_NOT_CANCELLABLE(
            "EMAIL_OUTBOX_NOT_CANCELLABLE",
            "Email outbox record cannot be cancelled in its current state",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Notification Item ─────────────────────────────────────────────────────

    NOTIFICATION_ITEM_NOT_FOUND(
            "NOTIFICATION_ITEM_NOT_FOUND",
            "Notification not found",
            HttpStatus.NOT_FOUND),

    NOTIFICATION_ITEM_ACCESS_DENIED(
            "NOTIFICATION_ITEM_ACCESS_DENIED",
            "Access to this notification is denied",
            HttpStatus.FORBIDDEN),

    // ── Dispatch ──────────────────────────────────────────────────────────────

    EMAIL_DISPATCH_EVENT_DEFINITION_NOT_FOUND(
            "EMAIL_DISPATCH_EVENT_DEFINITION_NOT_FOUND",
            "Event definition not found for notification dispatch",
            HttpStatus.NOT_FOUND),

    EMAIL_DISPATCH_EVENT_DEFINITION_NOT_ACTIVE(
            "EMAIL_DISPATCH_EVENT_DEFINITION_NOT_ACTIVE",
            "Event definition is not active for notification dispatch",
            HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    NotificationErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
