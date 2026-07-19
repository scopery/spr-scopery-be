package com.company.scopery.modules.collaboration.minutes.domain.model;
import com.company.scopery.modules.collaboration.minutes.domain.enums.MinutesStatus;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class MeetingMinutesDomainTest {
    @Test void approvedMinutesAreImmutable() {
        MeetingMinutes m = MeetingMinutes.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "s", null, null, null);
        m = m.submit(UUID.randomUUID()).approve(UUID.randomUUID());
        assertThat(m.status()).isEqualTo(MinutesStatus.APPROVED);
        MeetingMinutes approved = m;
        assertThatThrownBy(() -> approved.update("new", null, null, null)).isInstanceOf(IllegalStateException.class);
    }
}
