package com.company.scopery.modules.iam.role.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update the display name and description of an existing IAM role")
public record UpdateIamRoleRequest(
        @Schema(description = "Updated human-readable display name of the role (max 255 characters)", example = "AI Platform Operator v2")
        @NotBlank @Size(max = 255) String name,

        @Schema(description = "Updated description of what this role grants (max 2000 characters)", example = "Extended AI platform operator with additional deployment rights", nullable = true)
        @Size(max = 2000) String description) {}
