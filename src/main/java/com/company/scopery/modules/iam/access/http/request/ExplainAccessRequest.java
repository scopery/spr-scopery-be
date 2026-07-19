package com.company.scopery.modules.iam.access.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to explain why a subject has or does not have access to a resource action")
public record ExplainAccessRequest(
        @Schema(description = "Permission code identifying the capability", example = "AI_PLATFORM_MANAGE")
        @NotBlank String permissionCode,

        @Schema(description = "Action code within the permission (e.g. READ, WRITE, EXECUTE)", example = "EXECUTE")
        @NotBlank String actionCode,

        @Schema(description = "Resource type the access explanation targets", example = "AI_AGENT")
        @NotBlank String resourceType,

        @Schema(description = "Reference ID of the specific resource instance (UUID or external key)", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank String resourceRefId) {
}
