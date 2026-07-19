package com.company.scopery.modules.iam.authorization.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to obtain a detailed explanation of an authorization decision for auditing or debugging purposes")
public record ExplainAuthorizationRequest(
        @Schema(description = "Permission code identifying the capability to explain", example = "AI_PLATFORM_MANAGE")
        @NotBlank String permissionCode,

        @Schema(description = "Action code within the permission (e.g. READ, WRITE, EXECUTE)", example = "EXECUTE")
        @NotBlank String actionCode,

        @Schema(description = "Resource type the action targets", example = "AI_AGENT")
        @NotBlank String resourceType,

        @Schema(description = "Reference ID of the specific resource instance", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank String resourceRefId) {
}
