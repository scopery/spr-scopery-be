package com.company.scopery.modules.integrationhub.deadletter.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeadLetterEventDomainTest {

    @Test
    void fromWebhookFailure_createsOpenEvent() {
        UUID workspaceId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        DeadLetterEvent event = DeadLetterEvent.fromWebhookFailure(
                workspaceId, attemptId, "ISSUE_UPDATED", "delivery:" + attemptId, 5, "exhausted");

        assertThat(event.status()).isEqualTo("OPEN");
        assertThat(event.sourceType()).isEqualTo("WEBHOOK_DELIVERY");
        assertThat(event.failureCode()).isEqualTo("WEBHOOK_DELIVERY_EXHAUSTED");
        assertThat(event.attemptCount()).isEqualTo(5);
    }

    @Test
    void resolve_marksResolved() {
        DeadLetterEvent event = DeadLetterEvent.fromWebhookFailure(
                UUID.randomUUID(), UUID.randomUUID(), "X", "ref", 5, "fail");
        UUID actor = UUID.randomUUID();
        assertThat(event.resolve(actor).status()).isEqualTo("RESOLVED");
        assertThat(event.resolve(actor).resolvedBy()).isEqualTo(actor);
    }
}
