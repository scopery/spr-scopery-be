package com.company.scopery.modules.ratecard.costrole.application.response;

import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Cost role details returned by the API")
public record CostRoleResponse(
        @Schema(description = "Unique identifier of the cost role", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique code identifying the cost role", example = "SENIOR_DEV")
        String code,

        @Schema(description = "Display name of the cost role", example = "Senior Developer")
        String name,

        @Schema(description = "Optional description of the cost role", example = "Leads technical design and implementation", nullable = true)
        String description,

        @Schema(description = "Scope level of the cost role", example = "SYSTEM", allowableValues = {"SYSTEM", "ORGANIZATION", "WORKSPACE"})
        String scope,

        @Schema(description = "Organization this cost role belongs to (null for system-scoped roles)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace this cost role belongs to (null for system/org-scoped roles)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID workspaceId,

        @Schema(description = "Optional category grouping for the cost role", example = "Engineering", nullable = true)
        String category,

        @Schema(description = "Whether this cost role is a built-in system role", example = "false")
        boolean builtIn,

        @Schema(description = "Current lifecycle status of the cost role", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the cost role was archived (null if not archived)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant archivedAt,

        @Schema(description = "ID of the user who archived the cost role (null if not archived)", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID archivedBy,

        @Schema(description = "Optimistic locking version number", example = "1")
        int version,

        @Schema(description = "Timestamp when the cost role was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the cost role was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt
) {
    public static CostRoleResponse from(CostRole role) {
        return new CostRoleResponse(
                role.id(), role.code(), role.name(), role.description(), role.scope().name(),
                role.organizationId(), role.workspaceId(), role.category(), role.builtIn(),
                role.status().name(), role.archivedAt(), role.archivedBy(), role.version(),
                role.createdAt(), role.updatedAt()
        );
    }
}
