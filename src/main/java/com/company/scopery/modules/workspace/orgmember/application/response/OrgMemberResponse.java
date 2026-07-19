package com.company.scopery.modules.workspace.orgmember.application.response;

import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of an organization member")
public record OrgMemberResponse(
        @Schema(description = "Unique identifier of the membership record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the organization", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID organizationId,

        @Schema(description = "ID of the member user", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID userId,

        @Schema(description = "Membership type of the member", example = "MEMBER", allowableValues = {"OWNER", "ADMIN", "MEMBER"})
        String membershipType,

        @Schema(description = "Current status of the membership", example = "ACTIVE", allowableValues = {"ACTIVE", "SUSPENDED", "REMOVED"})
        String status,

        @Schema(description = "How the member was added to the organization", example = "DIRECT_ADD")
        String source,

        @Schema(description = "Timestamp when the user joined the organization", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant joinedAt,

        @Schema(description = "Timestamp when the membership was suspended", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant suspendedAt,

        @Schema(description = "Timestamp when the member was removed", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant removedAt,

        @Schema(description = "Optimistic locking version", example = "1")
        int version,

        @Schema(description = "Timestamp when the membership record was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the membership record was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static OrgMemberResponse from(OrgMember domain) {
        return new OrgMemberResponse(
                domain.id(),
                domain.organizationId(),
                domain.userId(),
                domain.membershipType().name(),
                domain.status().name(),
                domain.source().name(),
                domain.joinedAt(),
                domain.suspendedAt(),
                domain.removedAt(),
                domain.version(),
                domain.createdAt(),
                domain.updatedAt());
    }
}
