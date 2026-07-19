package com.company.scopery.modules.notification.notificationitem.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.notification.notificationitem.application.response.UnreadCountResponse;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MarkAllNotificationsReadAction {

    private final NotificationItemRepository repository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public MarkAllNotificationsReadAction(NotificationItemRepository repository,
                                           CurrentUserAuthorizationService currentUserAuthorizationService,
                                           NotificationActivityLogger activityLogger) {
        this.repository = repository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UnreadCountResponse execute() {
        UUID userId = currentUserAuthorizationService.resolveCurrentUser().id();
        int updated = repository.markAllRead(userId);
        activityLogger.logSuccess(NotificationEntityTypes.NOTIFICATION_ITEM, userId,
                NotificationActivityActions.MARK_ALL_NOTIFICATIONS_READ,
                "Marked " + updated + " notifications read");
        return new UnreadCountResponse(repository.countUnreadByRecipientUserId(userId));
    }
}
