package com.company.scopery.modules.workspace.orgmember.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Summary of a user's membership in an organization")
public record OrgMembershipSummaryResponse(
        @Schema(description = "ID of the organization", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID organizationId,

        @Schema(description = "Display name of the organization", example = "Acme Corporation")
        String organizationName,

        @Schema(description = "Membership type of the user in this organization", example = "MEMBER", allowableValues = {"OWNER", "ADMIN", "MEMBER"})
        String membershipType,

        @Schema(description = "Current membership status", example = "ACTIVE", allowableValues = {"ACTIVE", "SUSPENDED", "REMOVED"})
        String status) {
}
