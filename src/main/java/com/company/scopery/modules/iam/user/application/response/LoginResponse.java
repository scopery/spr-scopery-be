package com.company.scopery.modules.iam.user.application.response;

import com.company.scopery.modules.iam.user.domain.model.IamUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response returned after a successful login, containing basic user identity information")
public record LoginResponse(
        @Schema(description = "Unique identifier of the authenticated user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID   userId,

        @Schema(description = "Login username of the authenticated user", example = "john.doe")
        String username,

        @Schema(description = "Email address of the authenticated user", example = "john.doe@example.com")
        String email,

        @Schema(description = "Full display name of the authenticated user", example = "John Doe", nullable = true)
        String fullName) {

    public static LoginResponse from(IamUser user) {
        return new LoginResponse(user.id(), user.username().value(), user.email().value(), user.fullName());
    }
}
