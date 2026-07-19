package com.company.scopery.modules.collaboration.actionitem.domain.model;
import com.company.scopery.modules.collaboration.actionitem.domain.enums.ActionItemStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDate; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class MeetingActionItemDomainTest {
    @Test void completeAndOverdue() {
        MeetingActionItem a = MeetingActionItem.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null,
                "Follow up", null, "INTERNAL_USER", UUID.randomUUID(), LocalDate.now().minusDays(1), false);
        a = a.markOverdueIfNeeded(LocalDate.now());
        assertThat(a.status()).isEqualTo(ActionItemStatus.OVERDUE);
        a = a.complete(UUID.randomUUID(), "done");
        assertThat(a.status()).isEqualTo(ActionItemStatus.DONE);
        assertThat(a.completedAt()).isNotNull();
    }
    @Test void cannotDoubleLinkTask() {
        MeetingActionItem a = MeetingActionItem.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null,
                "T", null, null, null, null, false).withLinkedTask(UUID.randomUUID());
        assertThatThrownBy(() -> a.withLinkedTask(UUID.randomUUID())).isInstanceOf(IllegalStateException.class);
    }
}
