package com.company.scopery.modules.iam.permission.application.response;

import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Full representation of an IAM permission, including its action definitions and metadata")
public record IamPermissionResponse(
        @Schema(description = "Unique identifier of the permission", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique machine-readable code for this permission (e.g. AI_PLATFORM_MANAGE)", example = "AI_PLATFORM_MANAGE")
        String code,

        @Schema(description = "Module code that owns this permission (e.g. AIAGENT, IAM)", example = "AIAGENT")
        String moduleCode,

        @Schema(description = "Human-readable display name of the permission", example = "Manage AI Platform")
        String name,

        @Schema(description = "Detailed description of what this permission enables", example = "Full management access to the AI platform resources", nullable = true)
        String description,

        @Schema(description = "Scope level at which this permission operates (e.g. GLOBAL, WORKSPACE, RESOURCE)", example = "RESOURCE")
        String resourceScopeLevel,

        @Schema(description = "Data access policy defining the breadth of data access (e.g. ALL, OWN, NONE)", example = "ALL")
        String dataAccessPolicy,

        @Schema(description = "Functional category grouping permissions (e.g. ADMINISTRATIVE, OPERATIONAL, READ_ONLY)", example = "ADMINISTRATIVE")
        String permissionCategory,

        @Schema(description = "Subject types that can be assigned this permission (e.g. USER, TEAM)")
        List<String> assignableSubjectTypes,

        @Schema(description = "Risk level of granting this permission (e.g. LOW, MEDIUM, HIGH, CRITICAL)", example = "HIGH")
        String riskLevel,

        @Schema(description = "Current status of the permission", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "List of individual action definitions that belong to this permission")
        List<IamPermissionActionResponse> actions,

        @Schema(description = "Timestamp when this permission was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when this permission was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static IamPermissionResponse from(IamPermission permission,
                                             List<IamPermissionActionResponse> actions) {
        return new IamPermissionResponse(
                permission.id(),
                permission.code().value(),
                permission.moduleCode(),
                permission.name(),
                permission.description(),
                permission.resourceScopeLevel().name(),
                permission.dataAccessPolicy().name(),
                permission.permissionCategory().name(),
                permission.assignableSubjectTypes().stream()
                        .map(Enum::name)
                        .sorted()
                        .toList(),
                permission.riskLevel().name(),
                permission.status().name(),
                List.copyOf(actions),
                permission.createdAt(),
                permission.updatedAt());
    }
}
