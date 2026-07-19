package com.company.scopery.modules.integrationhub.sync.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderSyncAdapterTest {

    @Test
    void slackStub_noRemoteCall() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "SLACK", "ops", UUID.randomUUID()).activate();
        var result = new SlackSyncAdapter().pullStub(conn, null);
        assertThat(result.status()).isEqualTo("SYNC_STUB_NO_REMOTE");
        assertThat(result.liveRemoteCall()).isFalse();
        assertThat(result.cursorAdvanceAllowed()).isFalse();
    }

    @Test
    void jiraStub_noRemoteCall_andNotFakeSuccessLive() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "JIRA", "acme.atlassian.net", UUID.randomUUID()).activate();
        var result = new JiraSyncAdapter().pullStub(conn, null);
        assertThat(result.status()).isEqualTo("SYNC_STUB_NO_REMOTE");
        assertThat(result.liveRemoteCall()).isFalse();
        assertThat(result.message()).containsIgnoringCase("no live");
    }

    @Test
    void jiraWithoutCredential_unsupported() {
        var conn = IntegrationConnection.create(UUID.randomUUID(), "JIRA", "acme", null).activate();
        var result = new JiraSyncAdapter().pullStub(conn, null);
        assertThat(result.status()).isEqualTo("UNSUPPORTED_OPERATION");
    }
}
