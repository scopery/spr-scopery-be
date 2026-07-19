package com.company.scopery.modules.notification.notificationitem.application.service;

import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationPriority;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationSeverity;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationItemCreator {

    private static final Logger log = LoggerFactory.getLogger(NotificationItemCreator.class);

    private final NotificationItemRepository repository;
    private final NotificationActivityLogger activityLogger;

    public NotificationItemCreator(NotificationItemRepository repository,
                                    NotificationActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void createIfAbsent(UUID recipientUserId, EmailRule rule,
                               EmailNotificationTriggerPayload trigger,
                               String title, String bodyPreview, String dedupKey) {
        if (recipientUserId == null || dedupKey == null || dedupKey.isBlank()) {
            return;
        }
        if (repository.existsByRecipientUserIdAndDedupKey(recipientUserId, dedupKey)) {
            return;
        }
        NotificationSeverity severity = rule.mandatory()
                ? NotificationSeverity.SECURITY
                : NotificationSeverity.INFO;
        String safeTitle = title == null || title.isBlank() ? "Notification" : truncate(title, 255);
        String preview = bodyPreview == null ? null : truncate(bodyPreview, 200);
        try {
            NotificationItem item = NotificationItem.create(
                    recipientUserId,
                    trigger.eventDefinitionId(),
                    trigger.sourceSystem(),
                    null,
                    null,
                    null,
                    trigger.workspaceId(),
                    null,
                    safeTitle,
                    preview,
                    severity,
                    NotificationPriority.NORMAL,
                    null,
                    null,
                    dedupKey,
                    rule.mandatory(),
                    MDC.get("traceId"));
            item = repository.save(item);
            activityLogger.logSuccess(NotificationEntityTypes.NOTIFICATION_ITEM, item.id(),
                    NotificationActivityActions.NOTIFICATION_ITEM_CREATED,
                    "In-app notification created for user " + recipientUserId);
        } catch (DataIntegrityViolationException ex) {
            log.debug("[NotificationItemCreator] Dedup skip for recipient={} key={}", recipientUserId, dedupKey);
        }
    }

    private static String truncate(String value, int max) {
        String trimmed = value.trim();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }
}
