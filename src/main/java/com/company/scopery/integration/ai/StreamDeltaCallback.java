package com.company.scopery.integration.ai;

public interface StreamDeltaCallback {

    void onDelta(String textDelta, boolean isLast, String finishReason,
                 Integer inputTokens, Integer outputTokens);

    default void onToolCall(String toolCallId, String functionName, String argumentsJson) {}
}
