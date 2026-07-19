package com.company.scopery.modules.scope.criteria.domain.model;

import com.company.scopery.modules.scope.criteria.domain.enums.AcceptanceCriteriaStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AcceptanceCriteriaDomainTest {

    @Test
    void satisfy_marksCriteriaSatisfied() {
        AcceptanceCriteria c = AcceptanceCriteria.create(UUID.randomUUID(), UUID.randomUUID(), "FUNCTIONAL", "Done", null, true);
        AcceptanceCriteria satisfied = c.satisfy();
        assertThat(satisfied.status()).isEqualTo(AcceptanceCriteriaStatus.SATISFIED);
        assertThat(satisfied.isMet()).isTrue();
    }

    @Test
    void waive_marksCriteriaMetWithReason() {
        UUID actor = UUID.randomUUID();
        AcceptanceCriteria c = AcceptanceCriteria.create(UUID.randomUUID(), UUID.randomUUID(), "FUNCTIONAL", "Done", null, true);
        AcceptanceCriteria waived = c.waive(actor, "Client accepted verbally");
        assertThat(waived.status()).isEqualTo(AcceptanceCriteriaStatus.WAIVED);
        assertThat(waived.waivedBy()).isEqualTo(actor);
        assertThat(waived.isMet()).isTrue();
    }

    @Test
    void optionalOpenCriteria_isConsideredMet() {
        AcceptanceCriteria c = AcceptanceCriteria.create(UUID.randomUUID(), UUID.randomUUID(), "NICE_TO_HAVE", "Optional", null, false);
        assertThat(c.isMet()).isTrue();
    }
}
