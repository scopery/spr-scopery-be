package com.company.scopery.modules.externalparty.organization.domain.model;
import com.company.scopery.modules.externalparty.organization.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record ExternalOrganization(UUID id, UUID workspaceId, String code, String name, OrganizationType organizationType, OrganizationStatus status,
                                   String taxId, String website, String notes, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static ExternalOrganization create(UUID workspaceId, String code, String name, OrganizationType type, String taxId, String website, String notes) {
        Instant now = Instant.now();
        return new ExternalOrganization(UUID.randomUUID(), workspaceId, code, name, type, OrganizationStatus.ACTIVE, taxId, website, notes, null, null, 0, now, now);
    }
    public ExternalOrganization archive(UUID actorId) {
        return new ExternalOrganization(id, workspaceId, code, name, organizationType, OrganizationStatus.ARCHIVED, taxId, website, notes, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
