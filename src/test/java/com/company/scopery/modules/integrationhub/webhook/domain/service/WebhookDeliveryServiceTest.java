package com.company.scopery.modules.integrationhub.webhook.domain.service;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class WebhookDeliveryServiceTest {
    @Test void webhookDeadLetterAfterMaxAttempts() {
        assertThat(WebhookDeliveryService.nextStatus("FAILED", false, 5, 5)).isEqualTo("DEAD_LETTERED");
    }
    @Test void webhookPayloadMasksSensitiveFields() {
        assertThat(WebhookDeliveryService.redactPayload("{\"token\":\"abc123\",\"x\":1}")).contains("***").doesNotContain("abc123");
    }
}
