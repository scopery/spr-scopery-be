package com.company.scopery.modules.externalparty.relationship.domain.model;
import java.time.Instant; import java.util.UUID;
public record ProjectExternalPartyRelationship(UUID id, UUID projectId, UUID workspaceId, UUID organizationId, String relationshipType, String status, String notes, int version, Instant createdAt, Instant updatedAt) {
    public static ProjectExternalPartyRelationship create(UUID projectId, UUID workspaceId, UUID organizationId, String relationshipType, String notes) {
        Instant now = Instant.now();
        return new ProjectExternalPartyRelationship(UUID.randomUUID(), projectId, workspaceId, organizationId, relationshipType, "ACTIVE", notes, 0, now, now);
    }
}
