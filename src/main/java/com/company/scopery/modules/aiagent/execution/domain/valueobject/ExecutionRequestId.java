package com.company.scopery.modules.aiagent.execution.domain.valueobject;

public final class ExecutionRequestId {

    private static final int MAX_LENGTH = 150;

    private final String value;

    private ExecutionRequestId(String value) {
        this.value = value;
    }

    public static ExecutionRequestId of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Execution requestId must not be blank");
        }
        String trimmed = raw.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Execution requestId must not exceed " + MAX_LENGTH + " characters");
        }
        return new ExecutionRequestId(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionRequestId)) return false;
        return value.equals(((ExecutionRequestId) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
