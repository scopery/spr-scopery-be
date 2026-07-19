package com.company.scopery.modules.iam.right.application.response;

import com.company.scopery.modules.iam.right.domain.model.IamRight;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "IAM system right — a fine-grained operation privilege checked by security interceptors")
public record IamRightResponse(
        @Schema(description = "Unique identifier of the right", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique machine-readable code for this right (e.g. AI_PLATFORM_MANAGE)", example = "AI_PLATFORM_MANAGE")
        String code,

        @Schema(description = "Human-readable display name of the right", example = "Manage AI Platform")
        String name,

        @Schema(description = "Detailed description of what this right allows", example = "Grants full management access to AI platform resources", nullable = true)
        String description,

        @Schema(description = "Module code that owns this right (e.g. AIAGENT, IAM)", example = "AIAGENT")
        String module,

        @Schema(description = "Current status of the right", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Timestamp when this right was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when this right was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static IamRightResponse from(IamRight right) {
        return new IamRightResponse(
                right.id(), right.code().value(), right.name(),
                right.description(), right.module(), right.status().name(),
                right.createdAt(), right.updatedAt());
    }
}
