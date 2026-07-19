package com.company.scopery.modules.scope.deliverable.domain.model;

import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableStatus;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliverableDomainTest {

    @Test
    void accept_setsAcceptedStatusAndActor() {
        UUID actor = UUID.randomUUID();
        Deliverable d = Deliverable.create(UUID.randomUUID(), UUID.randomUUID(), DeliverableType.DOCUMENT,
                "D-1", "Spec", null, true);
        Deliverable accepted = d.accept(actor);

        assertThat(accepted.status()).isEqualTo(DeliverableStatus.ACCEPTED);
        assertThat(accepted.acceptedBy()).isEqualTo(actor);
        assertThat(accepted.acceptedAt()).isNotNull();
    }

    @Test
    void reopen_fromAccepted_setsReworkRequired() {
        UUID actor = UUID.randomUUID();
        Deliverable accepted = Deliverable.create(UUID.randomUUID(), UUID.randomUUID(), DeliverableType.DOCUMENT,
                "D-1", "Spec", null, true).accept(UUID.randomUUID());
        Deliverable reopened = accepted.reopen(actor, "Missing attachment");

        assertThat(reopened.status()).isEqualTo(DeliverableStatus.REWORK_REQUIRED);
        assertThat(reopened.reopenedBy()).isEqualTo(actor);
        assertThat(reopened.reopenReason()).isEqualTo("Missing attachment");
        assertThat(reopened.acceptedAt()).isNull();
        assertThat(reopened.acceptedBy()).isNull();
    }

    @Test
    void reopen_rejectsNonAcceptedDeliverable() {
        Deliverable draft = Deliverable.create(UUID.randomUUID(), UUID.randomUUID(), DeliverableType.DOCUMENT,
                "D-1", "Spec", null, true);

        assertThatThrownBy(() -> draft.reopen(UUID.randomUUID(), "reason"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("accepted");
    }
}
