package com.company.scopery.modules.iam.user.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a new IAM user account")
public record CreateIamUserRequest(
        @Schema(description = "Login username for the new account (3–100 characters)", example = "john.doe")
        @NotBlank @Size(min = 3, max = 100) String username,

        @Schema(description = "Email address for the new account (max 255 characters)", example = "john.doe@example.com")
        @NotBlank @Email @Size(max = 255) String email,

        @Schema(description = "Full display name of the user (max 255 characters)", example = "John Doe", nullable = true)
        @Size(max = 255) String fullName,

        @Schema(description = "Initial password (12–128 characters, must include at least one uppercase, one lowercase, and one numeric character)", example = "SecurePassw0rd!")
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{12,128}$",
                message = "must be 12-128 characters and include upper, lower, and numeric characters")
        String password) {}
