package com.company.scopery.modules.iam.user.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to confirm a password reset using a one-time token and a new password")
public record PasswordResetConfirmRequest(
        @Schema(description = "One-time password reset token received via email", example = "a1b2c3d4e5f6...")
        @NotBlank String token,

        @Schema(description = "New password (12–128 characters, must include at least one uppercase, one lowercase, and one numeric character)", example = "NewSecurePass1")
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{12,128}$") String newPassword) {
}
