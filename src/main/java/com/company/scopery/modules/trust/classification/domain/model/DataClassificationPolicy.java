package com.company.scopery.modules.trust.classification.domain.model;
import java.time.Instant; import java.util.UUID;
public record DataClassificationPolicy(UUID id, UUID workspaceId, String policyCode, String name, String defaultClassification,
        boolean enabled, int version, Instant createdAt) {
    public static DataClassificationPolicy create(UUID workspaceId, String code, String name, String defaultClassification) {
        return new DataClassificationPolicy(UUID.randomUUID(), workspaceId, code, name, defaultClassification, true, 0, Instant.now());
    }
    public DataClassificationPolicy withDefault(String classification) {
        return new DataClassificationPolicy(id, workspaceId, policyCode, name, classification, enabled, version, createdAt);
    }
}
