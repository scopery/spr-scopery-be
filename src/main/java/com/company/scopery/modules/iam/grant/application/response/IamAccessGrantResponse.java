package com.company.scopery.modules.iam.grant.application.response;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Full representation of an IAM access grant, including subject, resource, effect, scope, delegation, and lifecycle metadata")
public record IamAccessGrantResponse(
        @Schema(description = "Unique identifier of the grant", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Type of subject this grant is assigned to", example = "USER", allowableValues = {"USER", "TEAM"})
        String subjectType,

        @Schema(description = "ID of the subject (user or team) this grant is assigned to", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID subjectId,

        @Schema(description = "ID of the resource this grant applies to", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID resourceId,

        @Schema(description = "ID of the role associated with this grant (if role-based)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID roleId,

        @Schema(description = "Effect of this grant: allow or deny access", example = "ALLOW", allowableValues = {"ALLOW", "DENY"})
        String effect,

        @Schema(description = "Scope type that limits where the grant applies (e.g. WORKSPACE, GLOBAL)", example = "WORKSPACE", nullable = true)
        String scopeType,

        @Schema(description = "Reference ID of the scope entity (e.g. workspace ID)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID scopeRefId,

        @Schema(description = "Workspace this grant is confined to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "Kind/type of grant (e.g. ROLE_BASED, DIRECT)", example = "DIRECT")
        String kind,

        @Schema(description = "ID of the owner policy that sourced this grant (if policy-derived)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID sourcePolicyId,

        @Schema(description = "Whether the grantee can delegate this grant to other subjects", example = "false")
        boolean canDelegate,

        @Schema(description = "Maximum number of delegation hops allowed from this grant (0 = no delegation)", example = "0")
        int delegationDepth,

        @Schema(description = "Timestamp when this grant expires (null = no expiry)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant expiresAt,

        @Schema(description = "JSON string encoding additional conditions for this grant", example = "{\"ip\":\"10.0.0.0/8\"}", nullable = true)
        String conditionJson,

        @Schema(description = "Human-readable reason or justification for creating this grant", example = "Granted for quarterly audit project", nullable = true)
        String reason,

        @Schema(description = "Current status of the grant", example = "ACTIVE", allowableValues = {"ACTIVE", "REVOKED", "EXPIRED"})
        String status,

        @Schema(description = "ID of the user who granted this access", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID grantedBy,

        @Schema(description = "Timestamp when the grant was issued", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant grantedAt,

        @Schema(description = "Optimistic-lock version counter for this grant", example = "1")
        int version,

        @Schema(description = "Timestamp when this grant record was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when this grant record was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static IamAccessGrantResponse from(IamAccessGrant domain) {
        return new IamAccessGrantResponse(
                domain.id(),
                domain.subjectType().name(),
                domain.subjectId(),
                domain.resourceId(),
                domain.roleId(),
                domain.effect().name(),
                domain.scopeType() != null ? domain.scopeType().name() : null,
                domain.scopeRefId(),
                domain.workspaceId(),
                domain.kind().name(),
                domain.sourcePolicyId(),
                domain.canDelegate(),
                domain.delegationDepth(),
                domain.expiresAt(),
                domain.conditionJson(),
                domain.reason(),
                domain.status().name(),
                domain.grantedBy(),
                domain.grantedAt(),
                domain.version(),
                domain.createdAt(),
                domain.updatedAt()
        );
    }
}
