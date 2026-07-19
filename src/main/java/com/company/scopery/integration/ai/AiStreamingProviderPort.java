package com.company.scopery.integration.ai;

public interface AiStreamingProviderPort {
    String supportedProviderCode();
    void streamChat(AiStreamingRequest request, StreamDeltaCallback callback);
}
