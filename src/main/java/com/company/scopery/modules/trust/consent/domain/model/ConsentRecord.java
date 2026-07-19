package com.company.scopery.modules.trust.consent.domain.model;
import java.time.Instant; import java.util.UUID;
public record ConsentRecord(UUID id, UUID workspaceId, String consentType, String status, Instant givenAt, Instant withdrawnAt, Instant createdAt) {
    public static ConsentRecord given(UUID workspaceId, String type) {
        Instant now = Instant.now();
        return new ConsentRecord(UUID.randomUUID(), workspaceId, type, "GIVEN", now, null, now);
    }
    public ConsentRecord withdraw() {
        if (!"GIVEN".equals(status)) throw new IllegalStateException("invalid");
        return new ConsentRecord(id, workspaceId, consentType, "WITHDRAWN", givenAt, Instant.now(), createdAt);
    }
}
