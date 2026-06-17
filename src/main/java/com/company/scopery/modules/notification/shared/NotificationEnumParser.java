package com.company.scopery.modules.notification.shared;

import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailProviderType;
import com.company.scopery.modules.notification.emailrule.domain.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.EmailRuleScope;
import com.company.scopery.modules.notification.emailrule.domain.EmailRuleStatus;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateScope;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateStatus;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateVersionStatus;
import com.company.scopery.modules.notification.shared.error.NotificationErrorCatalog;

public final class NotificationEnumParser {

    private NotificationEnumParser() {}

    public static EmailTemplateScope parseTemplateScope(String value) {
        return parseRequired(EmailTemplateScope.class, value,
                NotificationErrorCatalog.EMAIL_TEMPLATE_SCOPE_INVALID.code(), "scope");
    }

    public static EmailTemplateStatus parseTemplateStatus(String value) {
        return parseOptional(EmailTemplateStatus.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_TEMPLATE_STATUS.code(), "status");
    }

    public static EmailTemplateVersionStatus parseVersionStatus(String value) {
        return parseOptional(EmailTemplateVersionStatus.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_TEMPLATE_VERSION_STATUS.code(), "status");
    }

    public static EmailRuleScope parseRuleScope(String value) {
        return parseRequired(EmailRuleScope.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_RULE_STATUS.code(), "scope");
    }

    public static EmailRuleStatus parseRuleStatus(String value) {
        return parseOptional(EmailRuleStatus.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_RULE_STATUS.code(), "status");
    }

    public static EmailRecipientStrategy parseRecipientStrategy(String value) {
        return parseRequired(EmailRecipientStrategy.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_RECIPIENT_STRATEGY.code(), "recipientStrategy");
    }

    public static EmailDeliveryStatus parseDeliveryStatus(String value) {
        return parseOptional(EmailDeliveryStatus.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_DELIVERY_STATUS.code(), "status");
    }

    public static EmailOutboxStatus parseOutboxStatus(String value) {
        return parseOptional(EmailOutboxStatus.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_OUTBOX_STATUS.code(), "status");
    }

    public static EmailProviderType parseProviderType(String value) {
        return parseRequired(EmailProviderType.class, value,
                NotificationErrorCatalog.INVALID_EMAIL_PROVIDER_TYPE.code(), "provider");
    }

    private static <E extends Enum<E>> E parseRequired(Class<E> type, String value,
                                                        String errorCode, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required [" + errorCode + "]");
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid " + fieldName + ": '" + value.trim() + "' [" + errorCode + "]");
        }
    }

    private static <E extends Enum<E>> E parseOptional(Class<E> type, String value,
                                                        String errorCode, String fieldName) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid " + fieldName + ": '" + value.trim() + "' [" + errorCode + "]");
        }
    }
}
