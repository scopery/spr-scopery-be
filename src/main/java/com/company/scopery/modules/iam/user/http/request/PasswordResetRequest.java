package com.company.scopery.modules.iam.user.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to initiate a password reset flow by sending a reset link to the registered email address")
public record PasswordResetRequest(
        @Schema(description = "Registered email address of the account to reset", example = "john.doe@example.com")
        @NotBlank @Email String email) {
}
