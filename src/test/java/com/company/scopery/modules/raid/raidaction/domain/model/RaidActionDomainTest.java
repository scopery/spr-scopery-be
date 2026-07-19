package com.company.scopery.modules.raid.raidaction.domain.model;

import com.company.scopery.modules.raid.raidaction.domain.enums.RaidActionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RaidActionDomainTest {

    @Test
    void withLinkedTask_setsLinkedTaskId() {
        UUID taskId = UUID.randomUUID();
        RaidAction action = RaidAction.create(
                UUID.randomUUID(), UUID.randomUUID(), "Mitigate risk", null, null, LocalDate.now());
        RaidAction linked = action.withLinkedTask(taskId);

        assertThat(linked.linkedTaskId()).isEqualTo(taskId);
        assertThat(linked.status()).isEqualTo(RaidActionStatus.OPEN);
        assertThat(linked.id()).isEqualTo(action.id());
    }

    @Test
    void cancel_setsCancelledStatus() {
        RaidAction action = RaidAction.create(
                UUID.randomUUID(), UUID.randomUUID(), "Mitigate risk", null, null, LocalDate.now());
        RaidAction cancelled = action.cancel();

        assertThat(cancelled.status()).isEqualTo(RaidActionStatus.CANCELLED);
    }
}
