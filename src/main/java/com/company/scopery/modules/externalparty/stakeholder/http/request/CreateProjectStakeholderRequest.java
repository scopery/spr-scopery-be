package com.company.scopery.modules.externalparty.stakeholder.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Request payload for adding a project stakeholder")
public record CreateProjectStakeholderRequest(
        @Schema(description = "Role of the stakeholder in the project", example = "SPONSOR", allowableValues = {"SPONSOR", "DECISION_MAKER", "INFLUENCER", "END_USER", "REVIEWER"})
        @NotBlank String stakeholderRole,

        @Schema(description = "ID of the external contact representing this stakeholder", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID contactId,

        @Schema(description = "ID of the external organization representing this stakeholder", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID organizationId,

        @Schema(description = "ID of an internal user representing this stakeholder", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID internalUserId,

        @Schema(description = "Whether this stakeholder is client-facing", example = "true", nullable = true)
        Boolean clientFacing) {}
