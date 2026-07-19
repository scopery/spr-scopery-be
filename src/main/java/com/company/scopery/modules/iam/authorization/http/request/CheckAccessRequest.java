package com.company.scopery.modules.iam.authorization.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to check whether a user has a specific right on a resource")
public record CheckAccessRequest(
        @Schema(description = "ID of the user whose access is being checked", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID userId,

        @Schema(description = "ID of the resource to check access against", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID resourceId,

        @Schema(description = "System right code to evaluate (e.g. AI_PLATFORM_MANAGE)", example = "AI_PLATFORM_MANAGE")
        @NotBlank String rightCode) {
}
