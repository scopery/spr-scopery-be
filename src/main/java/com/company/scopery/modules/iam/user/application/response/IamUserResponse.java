package com.company.scopery.modules.iam.user.application.response;

import com.company.scopery.modules.iam.user.domain.model.IamUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "IAM user record as returned by the API (excludes sensitive fields such as password hash)")
public record IamUserResponse(
        @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Login username of the user", example = "john.doe")
        String username,

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,

        @Schema(description = "Full display name of the user", example = "John Doe", nullable = true)
        String fullName,

        @Schema(description = "Current account status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
        String status,

        @Schema(description = "Timestamp when the user account was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the user account was last updated", example = "2026-07-17T03:00:00.000000Z")
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
