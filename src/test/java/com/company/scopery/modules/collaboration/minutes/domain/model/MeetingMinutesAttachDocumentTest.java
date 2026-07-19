package com.company.scopery.modules.collaboration.minutes.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class MeetingMinutesAttachDocumentTest {
    @Test
    void attachDocumentLinksOnce() {
        MeetingMinutes m = MeetingMinutes.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "s", null, null, "cv");
        UUID docId = UUID.randomUUID();
        UUID verId = UUID.randomUUID();
        m = m.attachDocument(docId, verId);
        assertThat(m.documentId()).isEqualTo(docId);
        assertThat(m.documentVersionId()).isEqualTo(verId);
        MeetingMinutes linked = m;
        assertThatThrownBy(() -> linked.attachDocument(UUID.randomUUID(), null)).isInstanceOf(IllegalStateException.class);
    }
}
