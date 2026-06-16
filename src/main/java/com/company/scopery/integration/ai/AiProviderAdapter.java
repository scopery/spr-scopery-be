package com.company.scopery.integration.ai;

public interface AiProviderAdapter {

    String supportedProviderCode();

    AiProviderResponse call(AiProviderRequest request);
}
