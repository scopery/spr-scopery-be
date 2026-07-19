package com.company.scopery.modules.iam.user.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to authenticate a user with username and password credentials")
public record LoginRequest(
        @Schema(description = "Login username of the user", example = "john.doe")
        @NotBlank String username,

        @Schema(description = "Password for the user account")
        @NotBlank String password) {
}
