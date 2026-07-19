package com.company.scopery.modules.projectnotification.subscription;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.ProjectSubscriptionType;
import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscription;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNotificationSubscriptionDomainTest {

    @Test
    void muteUnmute_success() {
        var sub = ProjectNotificationSubscription.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                ProjectSubscriptionType.PROJECT_WATCHER, false);
        assertEquals(SubscriptionStatus.ACTIVE, sub.status());
        sub = sub.mute();
        assertEquals(SubscriptionStatus.MUTED, sub.status());
        sub = sub.unmute();
        assertEquals(SubscriptionStatus.ACTIVE, sub.status());
    }

    @Test
    void archive_setsArchived() {
        var sub = ProjectNotificationSubscription.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                ProjectSubscriptionType.PROJECT_WATCHER, true);
        UUID actor = UUID.randomUUID();
        sub = sub.archive(actor);
        assertEquals(SubscriptionStatus.ARCHIVED, sub.status());
        assertEquals(actor, sub.archivedBy());
        assertNotNull(sub.archivedAt());
        assertTrue(sub.mandatory());
    }
}
