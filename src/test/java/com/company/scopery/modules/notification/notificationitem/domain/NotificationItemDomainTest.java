package com.company.scopery.modules.notification.notificationitem.domain;

import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationItemStatus;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationPriority;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationSeverity;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class NotificationItemDomainTest {

    @Test
    void markRead_setsStatusAndReadAt() {
        NotificationItem item = NotificationItem.create(
                UUID.randomUUID(), UUID.randomUUID(), "SYS", null, null,
                null, null, null, "Title", "Body",
                NotificationSeverity.INFO, NotificationPriority.NORMAL,
                null, null, "dedup-1", false, null);

        item.markRead();

        assertThat(item.status()).isEqualTo(NotificationItemStatus.READ);
        assertThat(item.readAt()).isNotNull();
    }

    @Test
    void dismiss_setsDismissed() {
        NotificationItem item = NotificationItem.create(
                UUID.randomUUID(), null, null, null, null,
                null, null, null, "Title", null,
                NotificationSeverity.SECURITY, NotificationPriority.HIGH,
                null, null, "dedup-2", true, null);

        item.dismiss();

        assertThat(item.status()).isEqualTo(NotificationItemStatus.DISMISSED);
        assertThat(item.dismissedAt()).isNotNull();
    }
}
