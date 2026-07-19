package com.company.scopery.modules.clientportal.invite.domain.model;
import com.company.scopery.modules.clientportal.invite.domain.enums.PortalInviteStatus;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalInvite(UUID id, UUID workspaceId, UUID projectId, String email, String inviteTokenHash, PortalInviteStatus status,
                                   Instant expiresAt, Instant acceptedAt, UUID invitedBy, UUID portalAccountId, int version, Instant createdAt, Instant updatedAt) {
    public static ExternalPortalInvite create(UUID workspaceId, UUID projectId, String email, String inviteTokenHash, Instant expiresAt, UUID invitedBy) {
        Instant now = Instant.now();
        return new ExternalPortalInvite(UUID.randomUUID(), workspaceId, projectId, email.toLowerCase(), inviteTokenHash, PortalInviteStatus.PENDING, expiresAt, null, invitedBy, null, 0, now, now);
    }
    public ExternalPortalInvite accept(UUID portalAccountId) {
        Instant now = Instant.now();
        return new ExternalPortalInvite(id, workspaceId, projectId, email, inviteTokenHash, PortalInviteStatus.ACCEPTED, expiresAt, now, invitedBy, portalAccountId, version, createdAt, now);
    }
    public boolean isUsable() {
        return status == PortalInviteStatus.PENDING && expiresAt.isAfter(Instant.now());
    }
}
