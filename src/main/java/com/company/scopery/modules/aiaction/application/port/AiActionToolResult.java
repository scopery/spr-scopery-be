package com.company.scopery.modules.aiaction.application.port;

public record AiActionToolResult(
        Status status,
        String resultVersionToken,
        String safeResultSummaryJson,
        String errorCode,
        Boolean retryable,
        String auditRef,
        String outboxRef
) {
    public enum Status { SUCCEEDED, FAILED, SKIPPED }

    public static AiActionToolResult succeeded(String resultVersionToken, String safeResultSummaryJson,
                                                String auditRef, String outboxRef) {
        return new AiActionToolResult(Status.SUCCEEDED, resultVersionToken, safeResultSummaryJson,
                null, null, auditRef, outboxRef);
    }

    public static AiActionToolResult failed(String errorCode, boolean retryable) {
        return new AiActionToolResult(Status.FAILED, null, null, errorCode, retryable, null, null);
    }

    public static AiActionToolResult skipped() {
        return new AiActionToolResult(Status.SKIPPED, null, null, null, null, null, null);
    }
}
