package com.company.scopery.modules.configuration.taxonomy.domain.model;
import java.time.Instant; import java.util.UUID;
public record TaxonomyTerm(UUID id, UUID taxonomyId, UUID parentTermId, String termCode, String label, String status,
        int version, Instant createdAt, Instant updatedAt) {
    public static TaxonomyTerm create(UUID taxonomyId, UUID parentId, String code, String label) {
        Instant now = Instant.now();
        return new TaxonomyTerm(UUID.randomUUID(), taxonomyId, parentId, code, label, "ACTIVE", 0, now, now);
    }
}
