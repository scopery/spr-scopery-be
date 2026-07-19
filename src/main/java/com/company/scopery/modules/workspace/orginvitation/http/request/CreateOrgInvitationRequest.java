package com.company.scopery.modules.workspace.orginvitation.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Schema(description = "Request payload for creating an organization invitation")
public record CreateOrgInvitationRequest(
        @Schema(description = "Email address of the person being invited", example = "jane.doe@example.com")
        @NotBlank @Email String inviteeEmail,

        @Schema(description = "Membership type to grant upon acceptance", example = "MEMBER", allowableValues = {"OWNER", "ADMIN", "MEMBER"}, nullable = true)
        String membershipType,

        @Schema(description = "Timestamp when the invitation expires", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant expiresAt) {
}
