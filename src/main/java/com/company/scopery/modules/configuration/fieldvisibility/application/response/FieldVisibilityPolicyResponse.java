package com.company.scopery.modules.configuration.fieldvisibility.application.response;

import com.company.scopery.modules.configuration.fieldvisibility.domain.model.FieldVisibilityPolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Field visibility policy details controlling which audiences can see a custom field")
public record FieldVisibilityPolicyResponse(
        @Schema(description = "Unique identifier of the visibility policy", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the workspace this policy applies to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "Identifier of the custom field definition controlled by this policy", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID customFieldDefinitionId,

        @Schema(description = "Type of audience this policy applies to", example = "CLIENT")
        String audienceType,

        @Schema(description = "Whether the field is visible to the specified audience", example = "true")
        boolean visible,

        @Schema(description = "Timestamp when the policy was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the policy was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static FieldVisibilityPolicyResponse from(FieldVisibilityPolicy p) {
        return new FieldVisibilityPolicyResponse(p.id(), p.workspaceId(), p.customFieldDefinitionId(), p.audienceType(), p.visible(), p.createdAt(), p.updatedAt());
    }
}
