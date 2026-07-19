package com.company.scopery.modules.integrationhub.sync.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.springframework.stereotype.Component;

@Component
public class GoogleDriveSyncAdapter implements ProviderSyncAdapter {
    @Override
    public String providerCode() {
        return "GOOGLE_DRIVE";
    }

    @Override
    public ProviderSyncResult pullStub(IntegrationConnection connection, String cursorValue) {
        if (connection.credentialReferenceId() == null) {
            return ProviderSyncResult.unsupported(providerCode(),
                    "Google Drive sync stub requires credential reference; live file sync not implemented");
        }
        return ProviderSyncResult.stubNoRemote(providerCode(),
                "Google Drive pull stub recorded — no live Drive files listed (capability: SYNC_STUB)");
    }
}
