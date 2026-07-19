package com.company.scopery.modules.aiagent.tool.application.port;

import java.util.List;

public record AiToolResult(
        boolean success,
        int resultCount,
        boolean truncated,
        String retrievalTraceId,
        List<AiToolResultItem> items,
        String errorCode,
        String errorSummary
) {
    public static AiToolResult success(int resultCount, boolean truncated,
                                       String retrievalTraceId, List<AiToolResultItem> items) {
        return new AiToolResult(true, resultCount, truncated, retrievalTraceId, items, null, null);
    }

    public static AiToolResult failure(String errorCode, String errorSummary) {
        return new AiToolResult(false, 0, false, null, List.of(), errorCode, errorSummary);
    }
}
