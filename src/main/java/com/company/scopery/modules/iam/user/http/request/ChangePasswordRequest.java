package com.company.scopery.modules.iam.user.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{12,128}$",
                message = "must be 12-128 characters and include upper, lower, and numeric characters")
        String newPassword) {
}
