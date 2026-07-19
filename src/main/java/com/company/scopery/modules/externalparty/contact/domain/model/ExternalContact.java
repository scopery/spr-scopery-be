package com.company.scopery.modules.externalparty.contact.domain.model;
import com.company.scopery.modules.externalparty.contact.domain.enums.ContactStatus;
import java.time.Instant; import java.util.UUID;
public record ExternalContact(UUID id, UUID workspaceId, UUID organizationId, String firstName, String lastName, String email, String phone,
                              String title, ContactStatus status, boolean primaryFlag, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static ExternalContact create(UUID workspaceId, UUID organizationId, String firstName, String lastName, String email, String phone, String title, boolean primaryFlag) {
        Instant now = Instant.now();
        return new ExternalContact(UUID.randomUUID(), workspaceId, organizationId, firstName, lastName, email, phone, title, ContactStatus.ACTIVE, primaryFlag, null, null, 0, now, now);
    }
}
