package com.company.scopery.modules.iam.grant.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request to add a permission-action entry to an existing IAM access grant. Provide either permissionActionId or both permissionCode + actionCode")
public record AddIamGrantPermissionActionRequest(
        @Schema(description = "ID of the permission action definition to attach (takes precedence if provided)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID permissionActionId,

        @Schema(description = "Permission code to resolve the action by code (used when permissionActionId is absent)", example = "AI_PLATFORM_MANAGE", nullable = true)
        @Size(max = 100) String permissionCode,

        @Schema(description = "Action code within the permission (used together with permissionCode)", example = "EXECUTE", nullable = true)
        @Size(max = 100) String actionCode) {
}
