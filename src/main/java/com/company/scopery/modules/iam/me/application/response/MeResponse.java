package com.company.scopery.modules.iam.me.application.response;

import com.company.scopery.modules.iam.me.domain.model.MeProfile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        String status,
        List<OrganizationMembershipSummary> organizationMemberships,
        SecurityState securityState,
        Instant createdAt) {

    public record OrganizationMembershipSummary(
            UUID organizationId,
            String organizationName,
            String membershipType,
            String status) {}

    public record SecurityState(
            boolean mfaEnabled,
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
