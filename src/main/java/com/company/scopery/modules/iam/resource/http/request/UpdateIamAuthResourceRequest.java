package com.company.scopery.modules.iam.resource.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update the display name and description of an existing IAM authorization resource")
public record UpdateIamAuthResourceRequest(
        @Schema(description = "Updated human-readable display name (max 255 characters)", example = "Sales Bot Agent v2")
        @NotBlank @Size(max = 255) String name,

        @Schema(description = "Updated description providing context for this resource (max 2000 characters)", example = "Upgraded AI agent for the sales team", nullable = true)
        @Size(max = 2000) String description) {}
