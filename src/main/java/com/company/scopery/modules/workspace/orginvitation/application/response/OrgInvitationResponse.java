package com.company.scopery.modules.workspace.orginvitation.application.response;

import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of an organization invitation")
public record OrgInvitationResponse(
        @Schema(description = "Unique identifier of the invitation", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the organization the invitation belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID organizationId,

        @Schema(description = "Email address of the invitee", example = "jane.doe@example.com")
        String inviteeEmail,

        @Schema(description = "User ID of the invitee if they already have an account", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID inviteeUserId,

        @Schema(description = "Membership type granted upon acceptance", example = "MEMBER", allowableValues = {"OWNER", "ADMIN", "MEMBER"})
        String membershipType,

        @Schema(description = "Current status of the invitation", example = "PENDING", allowableValues = {"PENDING", "ACCEPTED", "DECLINED", "EXPIRED", "REVOKED"})
        String status,

        @Schema(description = "User ID of the person who sent the invitation", example = "550e8400-e29b-41d4-a716-446655440003")
        UUID invitedBy,

        @Schema(description = "Raw invitation token returned only once at creation time", example = "abc123xyz", nullable = true)
        String token,

        @Schema(description = "Masked hint of the invitation token for display", example = "abc1***", nullable = true)
        String tokenHint,

        @Schema(description = "Timestamp when the invitation expires", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant expiresAt,

        @Schema(description = "Timestamp when the invitee responded to the invitation", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant respondedAt,

        @Schema(description = "Timestamp when the invitation was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static OrgInvitationResponse from(OrgInvitation domain, String rawTokenOnce) {
        return new OrgInvitationResponse(
                domain.id(),
                domain.organizationId(),
                domain.inviteeEmail(),
                domain.inviteeUserId(),
                domain.membershipType().name(),
                domain.status().name(),
                domain.invitedBy(),
                rawTokenOnce,
                domain.tokenHint(),
                domain.expiresAt(),
                domain.respondedAt(),
                domain.createdAt());
    }

    public static OrgInvitationResponse from(OrgInvitation domain) {
        return from(domain, null);
    }
}
