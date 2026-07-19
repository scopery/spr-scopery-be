package com.company.scopery.modules.integrationhub.connection.application.adapter;

import com.company.scopery.modules.integrationhub.connection.application.response.TestConnectionResult;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import org.springframework.stereotype.Component;

/**
 * Thin Jira config validation — does not call Atlassian APIs or claim OAuth/sync works.
 */
@Component
public class JiraConnectionTestAdapter implements ConnectionTestAdapter {

    @Override
    public String providerCode() {
        return "JIRA";
    }

    @Override
    public TestConnectionResult test(IntegrationConnection connection) {
        if (connection.credentialReferenceId() == null) {
            return TestConnectionResult.failedConfig(providerCode(),
                    "Jira connection requires a credential reference (API token / OAuth ref)");
        }
        if (connection.name() == null || connection.name().isBlank()) {
            return TestConnectionResult.failedConfig(providerCode(),
                    "Jira connection name (site label) is required");
        }
        return TestConnectionResult.successConfigOnly(providerCode(),
                "Jira config validated locally; no live Jira API / OAuth call performed");
    }
}
