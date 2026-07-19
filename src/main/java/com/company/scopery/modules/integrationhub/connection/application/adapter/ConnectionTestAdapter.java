package com.company.scopery.modules.integrationhub.connection.application.adapter;

import com.company.scopery.modules.integrationhub.connection.application.response.TestConnectionResult;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;

public interface ConnectionTestAdapter {
    String providerCode();
    TestConnectionResult test(IntegrationConnection connection);
}
