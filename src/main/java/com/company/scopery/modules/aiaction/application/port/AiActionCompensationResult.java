package com.company.scopery.modules.aiaction.application.port;

public record AiActionCompensationResult(
        Status status,
        String errorCode
) {
    public enum Status { COMPENSATED, FAILED, UNSUPPORTED }

    public static AiActionCompensationResult compensated() {
        return new AiActionCompensationResult(Status.COMPENSATED, null);
    }

    public static AiActionCompensationResult failed(String errorCode) {
        return new AiActionCompensationResult(Status.FAILED, errorCode);
    }

    public static AiActionCompensationResult unsupported() {
        return new AiActionCompensationResult(Status.UNSUPPORTED, null);
    }
}
