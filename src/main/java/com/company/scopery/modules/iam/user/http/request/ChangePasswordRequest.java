package com.company.scopery.modules.iam.user.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request for an authenticated user to change their own password")
public record ChangePasswordRequest(
        @Schema(description = "The user's current password for verification", example = "OldPassw0rd!")
        @NotBlank String currentPassword,

        @Schema(description = "New password (12–128 characters, must include at least one uppercase, one lowercase, and one numeric character)", example = "NewSecurePass1")
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{12,128}$",
                message = "must be 12-128 characters and include upper, lower, and numeric characters")
        String newPassword) {
}
