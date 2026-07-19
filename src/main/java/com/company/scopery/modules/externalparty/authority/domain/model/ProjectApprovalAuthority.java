package com.company.scopery.modules.externalparty.authority.domain.model;
import java.time.Instant; import java.util.UUID;
public record ProjectApprovalAuthority(UUID id, UUID projectId, UUID stakeholderId, String authorityType, String status, String notes, int version, Instant createdAt, Instant updatedAt) {
    public static ProjectApprovalAuthority create(UUID projectId, UUID stakeholderId, String authorityType, String notes) {
        Instant now = Instant.now();
        return new ProjectApprovalAuthority(UUID.randomUUID(), projectId, stakeholderId, authorityType, "ACTIVE", notes, 0, now, now);
    }
}
