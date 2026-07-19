package com.company.scopery.modules.notification.notificationitem.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.notification.notificationitem.application.response.NotificationItemResponse;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DismissNotificationAction {

    private final NotificationItemRepository repository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public DismissNotificationAction(NotificationItemRepository repository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      NotificationActivityLogger activityLogger) {
        this.repository = repository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public NotificationItemResponse execute(UUID id) {
        UUID userId = currentUserAuthorizationService.resolveCurrentUser().id();
        NotificationItem item = repository.findById(id)
                .orElseThrow(() -> NotificationExceptions.notificationItemNotFound(id));
        if (!item.recipientUserId().equals(userId)) {
            throw NotificationExceptions.notificationItemNotFound(id);
        }
        item.dismiss();
        item = repository.save(item);
        activityLogger.logSuccess(NotificationEntityTypes.NOTIFICATION_ITEM, item.id(),
                NotificationActivityActions.DISMISS_NOTIFICATION, "Notification dismissed");
        return NotificationItemResponse.from(item);
    }
}
