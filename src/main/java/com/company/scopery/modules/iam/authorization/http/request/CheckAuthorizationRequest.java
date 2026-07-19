package com.company.scopery.modules.iam.authorization.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Single authorization check: verifies whether the current principal may perform a specific action on a resource")
public record CheckAuthorizationRequest(
        @Schema(description = "Permission code identifying the capability", example = "AI_PLATFORM_MANAGE")
        @NotBlank String permissionCode,

        @Schema(description = "Action code within the permission (e.g. READ, WRITE, EXECUTE)", example = "EXECUTE")
        @NotBlank String actionCode,

        @Schema(description = "Resource type the action targets", example = "AI_AGENT")
        @NotBlank String resourceType,

        @Schema(description = "Reference ID of the specific resource instance (optional for global resources)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        String resourceRefId) {
}
