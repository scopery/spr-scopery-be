package com.company.scopery.modules.notification.advanced.preference.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class NotificationPreferenceProfileDomainTest {
    @Test void updateQuietHours() {
        var p = NotificationPreferenceProfile.create(UUID.randomUUID(), UUID.randomUUID());
        p = p.update("Asia/Ho_Chi_Minh", "DIGEST", true, true, "22:00", "07:00");
        assertThat(p.quietHoursEnabled()).isTrue();
        assertThat(p.digestEnabled()).isTrue();
        assertThat(p.timezone()).isEqualTo("Asia/Ho_Chi_Minh");
    }
}
