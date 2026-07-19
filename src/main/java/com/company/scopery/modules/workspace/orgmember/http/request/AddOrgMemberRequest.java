package com.company.scopery.modules.workspace.orgmember.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for adding a member to an organization")
public record AddOrgMemberRequest(
        @Schema(description = "ID of the user to add as an organization member", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID userId,

        @Schema(description = "Membership type to assign to the user", example = "MEMBER", allowableValues = {"OWNER", "ADMIN", "MEMBER"}, nullable = true)
        String membershipType) {
}
