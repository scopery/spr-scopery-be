package com.company.scopery.modules.externalparty.stakeholder.application.response;

import com.company.scopery.modules.externalparty.stakeholder.domain.model.ProjectStakeholder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a project stakeholder")
public record ProjectStakeholderResponse(
        @Schema(description = "Unique identifier of the stakeholder record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the project this stakeholder belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID projectId,

        @Schema(description = "Role of the stakeholder in the project", example = "SPONSOR", allowableValues = {"SPONSOR", "DECISION_MAKER", "INFLUENCER", "END_USER", "REVIEWER"})
        String stakeholderRole,

        @Schema(description = "Current status of the stakeholder", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Whether this stakeholder is client-facing", example = "true")
        boolean clientFacing,

        @Schema(description = "Timestamp when the stakeholder was added to the project", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static ProjectStakeholderResponse from(ProjectStakeholder e) {
        return new ProjectStakeholderResponse(e.id(), e.projectId(), e.stakeholderRole(), e.status().name(), e.clientFacing(), e.createdAt());
    }
}
