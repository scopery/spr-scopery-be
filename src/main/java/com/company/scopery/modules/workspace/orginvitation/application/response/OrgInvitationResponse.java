package com.company.scopery.modules.workspace.orginvitation.application.response;

import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;

import java.time.Instant;
import java.util.UUID;

public record OrgInvitationResponse(
        UUID id,
        UUID organizationId,
        String inviteeEmail,
        UUID inviteeUserId,
        String membershipType,
        String status,
        UUID invitedBy,
        Instant expiresAt,
        Instant respondedAt,
        Instant createdAt) {

    public static OrgInvitationResponse from(OrgInvitation domain) {
        return new OrgInvitationResponse(
                domain.id(),
                domain.organizationId(),
                domain.inviteeEmail(),
                domain.inviteeUserId(),
                domain.membershipType().name(),
                domain.status().name(),
                domain.invitedBy(),
                domain.expiresAt(),
                domain.respondedAt(),
                domain.createdAt());
    }
}
