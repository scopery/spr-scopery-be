package com.company.scopery.modules.iam.me.domain.model;

import com.company.scopery.modules.iam.user.domain.model.IamUser;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MeProfile(
        UUID userId,
        String username,
        String email,
        String fullName,
        String status,
        Instant createdAt,
        List<OrganizationMembership> organizationMemberships) {

    public record OrganizationMembership(
            UUID organizationId,
            String organizationName,
            String membershipType,
            String status) {
    }

    public static MeProfile of(IamUser user, List<OrganizationMembership> organizationMemberships) {
        return new MeProfile(
                user.id(),
                user.username().value(),
                user.email().value(),
                user.fullName(),
                user.status().name(),
                user.createdAt(),
                organizationMemberships);
    }
}
