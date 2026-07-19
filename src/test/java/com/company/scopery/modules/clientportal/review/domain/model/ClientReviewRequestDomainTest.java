package com.company.scopery.modules.clientportal.review.domain.model;

import com.company.scopery.modules.clientportal.review.domain.enums.ClientReviewStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientReviewRequestDomainTest {

    @Test
    void create_startsAsDraft() {
        var r = ClientReviewRequest.create(
                UUID.randomUUID(), UUID.randomUUID(), "DELIVERABLE", UUID.randomUUID(),
                "Review", UUID.randomUUID(), null);
        assertEquals(ClientReviewStatus.DRAFT, r.status());
    }

    @Test
    void decide_fromSent_becomesResponded() {
        Instant now = Instant.now();
        var sent = new ClientReviewRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "DELIVERABLE",
                UUID.randomUUID(), "Review", ClientReviewStatus.SENT, null,
                UUID.randomUUID(), null, 0, now, now);
        assertEquals(ClientReviewStatus.RESPONDED, sent.decide().status());
    }

    @Test
    void decide_fromDraft_throws() {
        var draft = ClientReviewRequest.create(
                UUID.randomUUID(), UUID.randomUUID(), "DELIVERABLE", UUID.randomUUID(),
                "Review", UUID.randomUUID(), null);
        assertThrows(IllegalStateException.class, draft::decide);
    }
}
