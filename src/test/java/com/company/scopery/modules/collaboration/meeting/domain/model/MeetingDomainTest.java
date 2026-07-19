package com.company.scopery.modules.collaboration.meeting.domain.model;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingStatus;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingType;
import org.junit.jupiter.api.Test;
import java.time.Instant; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class MeetingDomainTest {
    @Test void createStartCompleteLifecycle() {
        Meeting m = Meeting.create(UUID.randomUUID(), UUID.randomUUID(), null, "Status", null, MeetingType.GENERAL,
                Instant.now(), Instant.now().plusSeconds(3600), "UTC", null, null, UUID.randomUUID(), false);
        assertThat(m.status()).isEqualTo(MeetingStatus.SCHEDULED);
        m = m.start();
        assertThat(m.status()).isEqualTo(MeetingStatus.IN_PROGRESS);
        m = m.complete();
        assertThat(m.status()).isEqualTo(MeetingStatus.COMPLETED);
    }
    @Test void cancelBlocksCompletedMeeting() {
        Meeting m = Meeting.create(UUID.randomUUID(), UUID.randomUUID(), null, "X", null, MeetingType.GENERAL,
                Instant.now(), null, null, null, null, UUID.randomUUID(), false).start().complete();
        assertThatThrownBy(() -> m.cancel(UUID.randomUUID(), "n/a")).isInstanceOf(IllegalStateException.class);
    }
}
