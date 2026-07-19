package com.company.scopery.modules.integrationhub.sync.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.springframework.stereotype.Component;

@Component
public class SlackSyncAdapter implements ProviderSyncAdapter {
    @Override
    public String providerCode() {
        return "SLACK";
    }

    @Override
    public ProviderSyncResult pullStub(IntegrationConnection connection, String cursorValue) {
        if (connection.credentialReferenceId() == null) {
            return ProviderSyncResult.unsupported(providerCode(),
                    "Slack sync stub requires credential reference; live chat sync not implemented");
        }
        return ProviderSyncResult.stubNoRemote(providerCode(),
                "Slack pull stub recorded — no live conversations/users fetched (capability: SYNC_STUB)");
    }
}
