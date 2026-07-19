package com.company.scopery.modules.integrationhub.connection.application.adapter;

import com.company.scopery.modules.integrationhub.connection.application.response.TestConnectionResult;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.springframework.stereotype.Component;

/**
 * Config-shape validation only — does not call Slack APIs or claim full sync works.
 */
@Component
public class SlackConnectionTestAdapter implements ConnectionTestAdapter {

    @Override
    public String providerCode() {
        return "SLACK";
    }

    @Override
    public TestConnectionResult test(IntegrationConnection connection) {
        if (connection.credentialReferenceId() == null) {
            return TestConnectionResult.failedConfig(providerCode(),
                    "Slack connection requires a credential reference (bot token ref)");
        }
        return TestConnectionResult.successConfigOnly(providerCode(),
                "Slack config validated locally; no live Slack API call performed");
    }
}
