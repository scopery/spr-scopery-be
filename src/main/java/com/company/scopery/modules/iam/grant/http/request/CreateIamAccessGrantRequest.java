package com.company.scopery.modules.iam.grant.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Request to create a new IAM access grant assigning a role or direct permissions to a subject on a resource")
public record CreateIamAccessGrantRequest(
        @Schema(description = "Type of subject receiving the grant", example = "USER", allowableValues = {"USER", "TEAM"})
        @NotBlank String subjectType,

        @Schema(description = "ID of the subject (user or team) receiving the grant", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID subjectId,

        @Schema(description = "ID of the resource the grant applies to", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID resourceId,

        @Schema(description = "ID of the role to grant (optional for direct grants)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID roleId,

        @Schema(description = "Effect of the grant: ALLOW or DENY", example = "ALLOW", allowableValues = {"ALLOW", "DENY"}, nullable = true)
        String effect,

        @Schema(description = "Scope type limiting where the grant applies (e.g. WORKSPACE, GLOBAL)", example = "WORKSPACE", nullable = true)
        String scopeType,

        @Schema(description = "Reference ID of the scope entity (e.g. workspace ID)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID scopeRefId,

        @Schema(description = "Workspace this grant is confined to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "Future timestamp when this grant will automatically expire (null = no expiry)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        @Future Instant expiresAt) {
}
