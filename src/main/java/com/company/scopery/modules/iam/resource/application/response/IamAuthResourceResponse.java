package com.company.scopery.modules.iam.resource.application.response;

import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Schema(description = "IAM authorization resource — the entity against which access control is enforced")
public record IamAuthResourceResponse(
        @Schema(description = "Unique identifier of the authorization resource", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique machine-readable code for this resource (e.g. AI_AGENT_SALES_BOT)", example = "AI_AGENT_SALES_BOT")
        String code,

        @Schema(description = "Resource type categorizing this entity (e.g. AI_AGENT, WORKSPACE, PROJECT)", example = "AI_AGENT")
        String resourceType,

        @Schema(description = "Human-readable display name of the resource", example = "Sales Bot Agent")
        String name,

        @Schema(description = "Optional description providing context for this resource", example = "AI agent used by the sales team", nullable = true)
        String description,

        @Schema(description = "External reference ID linking this authorization resource to its domain entity", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID refId,

        @Schema(description = "ID of the user who owns this resource", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID ownerUserId,

        @Schema(description = "Organization this resource belongs to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace this resource is scoped to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "Visibility scope of this resource (e.g. PUBLIC, PRIVATE, WORKSPACE)", example = "PRIVATE", nullable = true)
        String visibility,

        @Schema(description = "ID of the parent resource in the resource hierarchy", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID parentResourceId,

        @Schema(description = "Current status of this resource", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Optimistic-lock version counter for this resource record", example = "1")
        int version,

        @Schema(description = "Timestamp when this resource was registered", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when this resource was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static IamAuthResourceResponse from(IamAuthResource r) {
        return new IamAuthResourceResponse(
                r.id(), r.code().value(), r.resourceType().name(),
                r.name(), r.description(),
                r.refId(), r.ownerUserId(), r.organizationId(), r.workspaceId(),
                r.visibility() != null ? r.visibility().name() : null,
                r.parentResourceId(),
                r.status().name(),
                r.version(),
                r.createdAt(), r.updatedAt());
    }
}
