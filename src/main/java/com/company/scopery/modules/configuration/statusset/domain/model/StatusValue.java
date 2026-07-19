package com.company.scopery.modules.configuration.statusset.domain.model;
import java.time.Instant; import java.util.UUID;
public record StatusValue(UUID id, UUID statusSetId, String valueCode, String label, String domainCategory, int sortOrder, String status, int version, Instant createdAt, Instant updatedAt) {
    public static StatusValue create(UUID setId, String code, String label, String domainCategory, int sortOrder) {
        Instant now = Instant.now();
        return new StatusValue(UUID.randomUUID(), setId, code, label, domainCategory, sortOrder, "ACTIVE", 0, now, now);
    }
}
