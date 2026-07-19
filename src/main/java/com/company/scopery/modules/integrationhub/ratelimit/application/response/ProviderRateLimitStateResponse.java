package com.company.scopery.modules.integrationhub.ratelimit.application.response;
import com.company.scopery.modules.integrationhub.ratelimit.domain.model.ProviderRateLimitState;
import java.time.Instant; import java.util.UUID;
public record ProviderRateLimitStateResponse(UUID id, UUID connectionId, String providerCode, String status,
        String limitName, Long remainingCount, Instant resetAt, Instant backoffUntil, Instant lastUpdatedAt) {
    public static ProviderRateLimitStateResponse from(ProviderRateLimitState s) {
        return new ProviderRateLimitStateResponse(s.id(), s.connectionId(), s.providerCode(), s.status(),
                s.limitName(), s.remainingCount(), s.resetAt(), s.backoffUntil(), s.lastUpdatedAt());
    }
}
