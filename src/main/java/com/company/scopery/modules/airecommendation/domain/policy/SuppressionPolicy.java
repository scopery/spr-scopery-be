package com.company.scopery.modules.airecommendation.domain.policy;

public final class SuppressionPolicy {

    public static final int MAX_DURATION_DAYS = 90;

    private SuppressionPolicy() {}

    public static void validateDuration(int durationDays) {
        if (durationDays < 1 || durationDays > MAX_DURATION_DAYS) {
            throw new IllegalArgumentException(
                    "Suppression duration must be between 1 and " + MAX_DURATION_DAYS + " days, got: " + durationDays);
        }
    }
}
