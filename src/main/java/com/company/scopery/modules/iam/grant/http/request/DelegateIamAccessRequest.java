package com.company.scopery.modules.iam.grant.http.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Request to delegate specific permission-actions on a resource to another subject")
public record DelegateIamAccessRequest(
        @Schema(description = "Type of subject receiving the delegated access", example = "USER", allowableValues = {"USER", "TEAM"})
        @NotBlank String subjectType,

        @Schema(description = "ID of the subject (user or team) receiving the delegated access", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID subjectId,

        @Schema(description = "Resource type the delegation applies to", example = "AI_AGENT")
        @NotBlank String resourceType,

        @Schema(description = "Reference ID of the specific resource instance being delegated", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID resourceRefId,

        @Schema(description = "How many additional hops the delegatee may further delegate (0 = no further delegation)", example = "0")
        @Min(0) int delegationDepth,

        @Schema(description = "Future timestamp when the delegated grant will automatically expire", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        @Future Instant expiresAt,

        @Schema(description = "Optional JSON condition that constrains when this delegation is effective", example = "{\"ip\":\"10.0.0.0/8\"}", nullable = true)
        JsonNode condition,

        @Schema(description = "Human-readable reason for the delegation", example = "Temporary access for quarterly review", nullable = true)
        String reason,

        @Schema(description = "List of permission-action pairs being delegated (at least one required)")
        @NotEmpty List<@Valid PermissionActionRequest> actions) {

    @Schema(description = "A single permission + action pair to include in the delegation")
    public record PermissionActionRequest(
            @Schema(description = "Permission code (e.g. AI_PLATFORM_MANAGE)", example = "AI_PLATFORM_MANAGE")
            @NotBlank String permissionCode,

            @Schema(description = "Action code within the permission (e.g. READ, WRITE, EXECUTE)", example = "EXECUTE")
            @NotBlank String actionCode) {}
}
