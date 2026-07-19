package com.company.scopery.modules.externalparty.relationship.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for creating a relationship between a project and an external party")
public record CreateProjectExternalPartyRelationshipRequest(
        @Schema(description = "ID of the external organization involved in the relationship", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID organizationId,

        @Schema(description = "Type of the relationship between the project and the external party", example = "PRIMARY_CLIENT", allowableValues = {"PRIMARY_CLIENT", "SUBCONTRACTOR", "PARTNER", "REGULATOR"})
        @NotBlank String relationshipType,

        @Schema(description = "Additional notes about the relationship", example = "Main funding client for Phase 1", nullable = true)
        String notes) {}
