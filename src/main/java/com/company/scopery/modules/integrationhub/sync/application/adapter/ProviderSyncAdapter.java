package com.company.scopery.modules.integrationhub.sync.application.adapter;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;

/**
 * Provider-specific sync skeleton. Implementations must not fake live remote success.
 */
public interface ProviderSyncAdapter {

    String providerCode();

    /**
     * @return structured result with capability honesty flags
     */
    ProviderSyncResult pullStub(IntegrationConnection connection, String cursorValue);
}
