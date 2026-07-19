package com.company.scopery.modules.integrationhub.sync.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.springframework.stereotype.Component;

/**
 * Jira sync skeleton: persists attempt honestly as stub / unsupported — never fakes issue import success.
 */
@Component
public class JiraSyncAdapter implements ProviderSyncAdapter {
    @Override
    public String providerCode() {
        return "JIRA";
    }

    @Override
    public ProviderSyncResult pullStub(IntegrationConnection connection, String cursorValue) {
        if (connection.credentialReferenceId() == null) {
            return ProviderSyncResult.unsupported(providerCode(),
                    "Jira sync requires credential reference; OAuth/token store + live JQL not available");
        }
        return ProviderSyncResult.stubNoRemote(providerCode(),
                "Jira pull stub recorded — no live issues fetched (capability: SYNC_STUB; OAuth/live API deferred)");
    }
}
