package com.company.scopery.modules.integrationhub.connection.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JiraConnectionTestAdapterTest {

    private final JiraConnectionTestAdapter adapter = new JiraConnectionTestAdapter();

    @Test
    void successConfigOnly_withCredential() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "JIRA", "site", UUID.randomUUID()).activate();
        var result = adapter.test(conn);
        assertThat(result.status()).isEqualTo("SUCCESS_CONFIG_ONLY");
        assertThat(result.configValid()).isTrue();
    }

    @Test
    void failed_withoutCredential() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "JIRA", "site", null).activate();
        var result = adapter.test(conn);
        assertThat(result.status()).isEqualTo("FAILED_CONFIG");
    }
}
