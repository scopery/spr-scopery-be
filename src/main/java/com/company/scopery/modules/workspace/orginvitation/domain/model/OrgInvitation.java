package com.company.scopery.modules.workspace.orginvitation.domain.model;

import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;

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
        String tokenHash,
        String tokenHint,
        Instant expiresAt,
        Instant respondedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgInvitation create(UUID organizationId, String inviteeEmail,
                                        OrgMembershipType membershipType, UUID invitedBy,
                                        String rawToken, Instant expiresAt) {
        Instant now = Instant.now();
        return new OrgInvitation(UUID.randomUUID(), organizationId, inviteeEmail, null,
                membershipType, OrgInvitationStatus.PENDING, invitedBy,
                InvitationCodeHasher.hash(rawToken), InvitationCodeHasher.hint(rawToken),
                expiresAt, null, now, now);
    }

    public OrgInvitation accept(UUID inviteeUserId) {
        return new OrgInvitation(id, organizationId, inviteeEmail, inviteeUserId,
                membershipType, OrgInvitationStatus.ACCEPTED, invitedBy, tokenHash, tokenHint,
                expiresAt, Instant.now(), createdAt, Instant.now());
    }

    public OrgInvitation cancel() {
        return new OrgInvitation(id, organizationId, inviteeEmail, inviteeUserId,
                membershipType, OrgInvitationStatus.CANCELLED, invitedBy, tokenHash, tokenHint,
                expiresAt, Instant.now(), createdAt, Instant.now());
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
