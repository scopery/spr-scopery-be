package com.company.scopery.modules.raid.decision.domain.model;

import com.company.scopery.modules.raid.decision.domain.enums.DecisionCategory;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionDomainTest {

    @Test
    void create_startsAsProposed() {
        DecisionRecord d = DecisionRecord.create(
                UUID.randomUUID(), UUID.randomUUID(), "D-1", "Choose stack", DecisionCategory.TECHNICAL, "Need decision");
        assertThat(d.status()).isEqualTo(DecisionStatus.PROPOSED);
        assertThat(d.outcome()).isNull();
        assertThat(d.decidedBy()).isNull();
    }

    @Test
    void decide_setsDecidedStatusAndOutcome() {
        UUID actor = UUID.randomUUID();
        DecisionRecord d = DecisionRecord.create(
                UUID.randomUUID(), UUID.randomUUID(), "D-1", "Choose stack", DecisionCategory.TECHNICAL, "Need decision");
        DecisionRecord decided = d.decide(actor, "Use Java");
        assertThat(decided.status()).isEqualTo(DecisionStatus.DECIDED);
        assertThat(decided.outcome()).isEqualTo("Use Java");
        assertThat(decided.decidedBy()).isEqualTo(actor);
        assertThat(decided.decidedAt()).isNotNull();
    }

    @Test
    void reject_setsRejectedStatusAndReasonAsOutcome() {
        UUID actor = UUID.randomUUID();
        DecisionRecord d = DecisionRecord.create(
                UUID.randomUUID(), UUID.randomUUID(), "D-1", "Choose stack", DecisionCategory.TECHNICAL, "Need decision");
        DecisionRecord rejected = d.reject(actor, "Insufficient data");
        assertThat(rejected.status()).isEqualTo(DecisionStatus.REJECTED);
        assertThat(rejected.outcome()).isEqualTo("Insufficient data");
        assertThat(rejected.decidedBy()).isEqualTo(actor);
    }

    @Test
    void archive_setsArchivedStatus() {
        DecisionRecord d = DecisionRecord.create(
                UUID.randomUUID(), UUID.randomUUID(), "D-1", "Choose stack", DecisionCategory.TECHNICAL, "Need decision")
                .decide(UUID.randomUUID(), "Use Java")
                .archive();
        assertThat(d.status()).isEqualTo(DecisionStatus.ARCHIVED);
        assertThat(d.outcome()).isEqualTo("Use Java");
    }

    @Test
    void supersede_setsSupersededStatusAndReplacementId() {
        UUID replacementId = UUID.randomUUID();
        DecisionRecord d = DecisionRecord.create(
                UUID.randomUUID(), UUID.randomUUID(), "D-1", "Choose stack", DecisionCategory.TECHNICAL, "Need decision");
        DecisionRecord superseded = d.supersede(replacementId);

        assertThat(superseded.status()).isEqualTo(DecisionStatus.SUPERSEDED);
        assertThat(superseded.supersededByDecisionId()).isEqualTo(replacementId);
    }
}
