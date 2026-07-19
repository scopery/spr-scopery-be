package com.company.scopery.integration.ai;

@FunctionalInterface
public interface StreamDeltaCallback {
    void onDelta(String textDelta, boolean isLast, String finishReason,
                 Integer inputTokens, Integer outputTokens);
}
