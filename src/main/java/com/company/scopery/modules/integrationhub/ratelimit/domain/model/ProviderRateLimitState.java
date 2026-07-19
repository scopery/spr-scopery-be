package com.company.scopery.modules.integrationhub.ratelimit.domain.model;
import java.time.Instant; import java.util.UUID;
public record ProviderRateLimitState(UUID id, UUID workspaceId, UUID connectionId, String providerCode,
        String status, String limitName, Long remainingCount, Instant resetAt, Instant backoffUntil,
        Instant lastUpdatedAt, int version, Instant createdAt, Instant updatedAt) {

    public static ProviderRateLimitState create(UUID workspaceId, UUID connectionId, String providerCode) {
        Instant now = Instant.now();
        return new ProviderRateLimitState(UUID.randomUUID(), workspaceId, connectionId, providerCode,
                "OK", null, null, null, null, now, 0, now, now);
    }

    public ProviderRateLimitState markRateLimited(Instant resetAt, Instant backoffUntil) {
        Instant now = Instant.now();
        return new ProviderRateLimitState(id, workspaceId, connectionId, providerCode, "RATE_LIMITED",
                limitName, remainingCount, resetAt, backoffUntil, now, version, createdAt, now);
    }

    public ProviderRateLimitState markOk() {
        Instant now = Instant.now();
        return new ProviderRateLimitState(id, workspaceId, connectionId, providerCode, "OK",
                limitName, remainingCount, resetAt, null, now, version, createdAt, now);
    }
}
