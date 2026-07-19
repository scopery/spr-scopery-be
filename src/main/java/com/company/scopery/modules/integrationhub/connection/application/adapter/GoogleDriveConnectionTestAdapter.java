package com.company.scopery.modules.integrationhub.connection.application.adapter;

import com.company.scopery.modules.integrationhub.connection.application.response.TestConnectionResult;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.springframework.stereotype.Component;

/**
 * Config-shape validation only — does not call Google Drive APIs or claim full sync works.
 */
@Component
public class GoogleDriveConnectionTestAdapter implements ConnectionTestAdapter {

    @Override
    public String providerCode() {
        return "GOOGLE_DRIVE";
    }

    @Override
    public TestConnectionResult test(IntegrationConnection connection) {
        if (connection.credentialReferenceId() == null) {
            return TestConnectionResult.failedConfig(providerCode(),
                    "Google Drive connection requires a credential reference (OAuth/service account ref)");
        }
        return TestConnectionResult.successConfigOnly(providerCode(),
                "Google Drive config validated locally; no live Drive API call performed");
    }
}
