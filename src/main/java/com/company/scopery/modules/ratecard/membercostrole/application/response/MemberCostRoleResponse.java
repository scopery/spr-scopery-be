package com.company.scopery.modules.ratecard.membercostrole.application.response;

import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleAssignment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Workspace member cost role assignment details returned by the API")
public record MemberCostRoleResponse(
        @Schema(description = "Unique identifier of the assignment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Workspace the assignment belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "Workspace member this assignment is for", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID workspaceMemberId,

        @Schema(description = "User ID associated with the workspace member", example = "550e8400-e29b-41d4-a716-446655440003")
        UUID userId,

        @Schema(description = "Cost role assigned to the workspace member", example = "550e8400-e29b-41d4-a716-446655440004")
        UUID costRoleId,

        @Schema(description = "Whether this is the default cost role for the workspace member", example = "true")
        boolean isDefault,

        @Schema(description = "Date from which this assignment is effective", example = "2026-01-01")
        LocalDate effectiveFrom,

        @Schema(description = "Date on which this assignment expires (null if open-ended)", example = "2026-12-31", nullable = true)
        LocalDate effectiveTo,

        @Schema(description = "Current lifecycle status of the assignment", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the assignment was archived (null if not archived)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant archivedAt,

        @Schema(description = "ID of the user who archived the assignment (null if not archived)", example = "550e8400-e29b-41d4-a716-446655440005", nullable = true)
        UUID archivedBy,

        @Schema(description = "Optimistic locking version number", example = "1")
        int version,

        @Schema(description = "Timestamp when the assignment was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the assignment was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt
) {
    public static MemberCostRoleResponse from(WorkspaceMemberCostRoleAssignment a) {
        return new MemberCostRoleResponse(a.id(), a.workspaceId(), a.workspaceMemberId(), a.userId(), a.costRoleId(),
                a.isDefault(), a.effectiveFrom(), a.effectiveTo(), a.status().name(), a.archivedAt(), a.archivedBy(),
                a.version(), a.createdAt(), a.updatedAt());
    }
}
