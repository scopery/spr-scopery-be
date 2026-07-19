package com.company.scopery.modules.iam.me.application.response;

import com.company.scopery.modules.iam.me.domain.model.MeProfile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Profile of the currently authenticated user, including organization memberships and security state")
public record MeResponse(
        @Schema(description = "Unique identifier of the authenticated user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Login username of the user", example = "john.doe")
        String username,

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,

        @Schema(description = "Full display name of the user", example = "John Doe", nullable = true)
        String fullName,

        @Schema(description = "Current account status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
        String status,

        @Schema(description = "List of organizations the user belongs to")
        List<OrganizationMembershipSummary> organizationMemberships,

        @Schema(description = "Security flags for the current session")
        SecurityState securityState,

        @Schema(description = "Timestamp when the user account was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    @Schema(description = "Summary of the user's membership in a single organization")
    public record OrganizationMembershipSummary(
            @Schema(description = "ID of the organization", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID organizationId,

            @Schema(description = "Display name of the organization", example = "Acme Corp")
            String organizationName,

            @Schema(description = "Membership type within the organization (e.g. OWNER, MEMBER)", example = "MEMBER")
            String membershipType,

            @Schema(description = "Status of this membership", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
            String status) {}

    @Schema(description = "Security-related state flags for the current user")
    public record SecurityState(
            @Schema(description = "Whether multi-factor authentication is enabled for this user", example = "false")
            boolean mfaEnabled,

            @Schema(description = "Whether the user must change their password on next login", example = "false")
            boolean passwordChangeRequired) {}

    public static MeResponse from(MeProfile profile) {
        return new MeResponse(
                profile.userId(),
                profile.username(),
                profile.email(),
                profile.fullName(),
                profile.status(),
                profile.organizationMemberships().stream()
                        .map(m -> new OrganizationMembershipSummary(
                                m.organizationId(), m.organizationName(),
                                m.membershipType(), m.status()))
                        .toList(),
                new SecurityState(false, false),
                profile.createdAt());
    }
}
