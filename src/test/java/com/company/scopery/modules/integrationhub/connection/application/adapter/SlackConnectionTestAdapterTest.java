package com.company.scopery.modules.integrationhub.connection.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SlackConnectionTestAdapterTest {
    private final SlackConnectionTestAdapter adapter = new SlackConnectionTestAdapter();

    @Test
    void test_withoutCredential_failsConfig() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "SLACK", "Slack", null);
        var result = adapter.test(conn);
        assertThat(result.configValid()).isFalse();
        assertThat(result.status()).isEqualTo("FAILED_CONFIG");
    }

    @Test
    void test_withCredential_successConfigOnly() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "SLACK", "Slack", UUID.randomUUID()).activate();
        var result = adapter.test(conn);
        assertThat(result.configValid()).isTrue();
        assertThat(result.status()).isEqualTo("SUCCESS_CONFIG_ONLY");
    }
}
