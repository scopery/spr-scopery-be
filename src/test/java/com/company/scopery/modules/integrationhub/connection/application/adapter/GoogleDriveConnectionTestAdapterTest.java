package com.company.scopery.modules.integrationhub.connection.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleDriveConnectionTestAdapterTest {
    private final GoogleDriveConnectionTestAdapter adapter = new GoogleDriveConnectionTestAdapter();

    @Test
    void test_withCredential_successConfigOnly() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "GOOGLE_DRIVE", "Drive", UUID.randomUUID()).activate();
        var result = adapter.test(conn);
        assertThat(result.configValid()).isTrue();
        assertThat(result.status()).isEqualTo("SUCCESS_CONFIG_ONLY");
    }
}
