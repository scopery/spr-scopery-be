package com.company.scopery.modules.iam.user.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update the profile information of an existing IAM user")
public record UpdateIamUserRequest(
        @Schema(description = "Updated full display name of the user (max 255 characters)", example = "John A. Doe", nullable = true)
        @Size(max = 255) String fullName) {}
