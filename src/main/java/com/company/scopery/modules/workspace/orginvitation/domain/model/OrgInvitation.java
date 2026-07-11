package com.company.scopery.modules.workspace.orginvitation.domain.model;

import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;

import java.time.Instant;
import java.util.UUID;

public record OrgInvitation(
        UUID id,
        UUID organizationId,
        String inviteeEmail,
        UUID inviteeUserId,
        OrgMembershipType membershipType,
        OrgInvitationStatus status,
        UUID invitedBy,
        String token,
        Instant expiresAt,
        Instant respondedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgInvitation create(UUID organizationId, String inviteeEmail,
                                        OrgMembershipType membershipType, UUID invitedBy,
                                        String token, Instant expiresAt) {
        Instant now = Instant.now();
        return new OrgInvitation(UUID.randomUUID(), organizationId, inviteeEmail, null,
                membershipType, OrgInvitationStatus.PENDING, invitedBy, token, expiresAt,
                null, now, now);
    }

    public OrgInvitation accept(UUID inviteeUserId) {
        return new OrgInvitation(id, organizationId, inviteeEmail, inviteeUserId,
                membershipType, OrgInvitationStatus.ACCEPTED, invitedBy, token, expiresAt,
                Instant.now(), createdAt, Instant.now());
    }

    public OrgInvitation cancel() {
        return new OrgInvitation(id, organizationId, inviteeEmail, inviteeUserId,
                membershipType, OrgInvitationStatus.CANCELLED, invitedBy, token, expiresAt,
                Instant.now(), createdAt, Instant.now());
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
