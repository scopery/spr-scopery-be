package com.company.scopery.modules.notification.notificationitem.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.notification.notificationitem.application.action.DismissNotificationAction;
import com.company.scopery.modules.notification.notificationitem.application.action.MarkNotificationReadAction;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationItemStatus;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationPriority;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationSeverity;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationItemActionTest {

    @Mock private NotificationItemRepository repository;
    @Mock private CurrentUserAuthorizationService currentUserAuthorizationService;
    @Mock private NotificationActivityLogger activityLogger;

    private MarkNotificationReadAction markReadAction;
    private DismissNotificationAction dismissAction;
    private final UUID userId = UUID.randomUUID();
    private final UUID otherUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        markReadAction = new MarkNotificationReadAction(repository, currentUserAuthorizationService, activityLogger);
        dismissAction = new DismissNotificationAction(repository, currentUserAuthorizationService, activityLogger);
        IamUser currentUser = IamUser.of(userId, Username.of("user"), EmailAddress.of("u@example.com"),
                "User", "hash", IamUserStatus.ACTIVE, Instant.now(), Instant.now());
        when(currentUserAuthorizationService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void markRead_ownNotification_success() {
        NotificationItem item = ownItem();
        when(repository.findById(item.id())).thenReturn(Optional.of(item));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = markReadAction.execute(item.id());

        assertThat(response.status()).isEqualTo(NotificationItemStatus.READ.name());
    }

    @Test
    void markRead_otherUser_notFound() {
        NotificationItem item = NotificationItem.create(
                otherUserId, null, null, null, null, null, null, null,
                "Title", null, NotificationSeverity.INFO, NotificationPriority.NORMAL,
                null, null, "d1", false, null);
        when(repository.findById(item.id())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> markReadAction.execute(item.id()))
                .isInstanceOf(AppException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void dismiss_ownNotification_success() {
        NotificationItem item = ownItem();
        when(repository.findById(item.id())).thenReturn(Optional.of(item));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = dismissAction.execute(item.id());

        assertThat(response.status()).isEqualTo(NotificationItemStatus.DISMISSED.name());
    }

    private NotificationItem ownItem() {
        return NotificationItem.create(
                userId, null, null, null, null, null, null, null,
                "Title", "preview", NotificationSeverity.INFO, NotificationPriority.NORMAL,
                null, null, "dedup-own", false, null);
    }
}
