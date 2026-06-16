package com.company.scopery.modules.iam.user.application.response;

import com.company.scopery.modules.iam.user.domain.IamUser;

import java.time.Instant;
import java.util.UUID;

public record IamUserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamUserResponse from(IamUser user) {
        return new IamUserResponse(
                user.id(),
                user.username().value(),
                user.email().value(),
                user.fullName(),
                user.status().name(),
                user.createdAt(),
                user.updatedAt());
    }
}
