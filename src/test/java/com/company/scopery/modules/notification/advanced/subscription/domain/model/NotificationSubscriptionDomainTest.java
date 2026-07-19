package com.company.scopery.modules.notification.advanced.subscription.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class NotificationSubscriptionDomainTest {
    @Test void createAndUnsubscribe() {
        var s = NotificationSubscription.create(UUID.randomUUID(), UUID.randomUUID(), "PROJECT", UUID.randomUUID(), "ALL");
        assertThat(s.status()).isEqualTo("ACTIVE");
        assertThat(s.unsubscribe().status()).isEqualTo("INACTIVE");
    }
}
