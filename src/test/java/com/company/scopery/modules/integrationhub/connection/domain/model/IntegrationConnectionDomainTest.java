package com.company.scopery.modules.integrationhub.connection.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class IntegrationConnectionDomainTest {
    @Test void disableConnection_blocksJobs() {
        var c = IntegrationConnection.create(UUID.randomUUID(), "CSV", "c", null).activate().disable();
        assertThat(c.canRunJobs()).isFalse();
    }
    @Test void healthCheckFailure_marksDegraded() {
        var c = IntegrationConnection.create(UUID.randomUUID(), "CSV", "c", null).activate().withHealth("ERROR");
        assertThat(c.status()).isEqualTo("DEGRADED");
    }
}
