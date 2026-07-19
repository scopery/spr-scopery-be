package com.company.scopery.modules.externalparty.authority.application.response;

import com.company.scopery.modules.externalparty.authority.domain.model.ProjectApprovalAuthority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a project approval authority assignment")
public record ProjectApprovalAuthorityResponse(
        @Schema(description = "Unique identifier of the approval authority record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the project this authority belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID projectId,

        @Schema(description = "ID of the stakeholder who holds this authority", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID stakeholderId,

        @Schema(description = "Type of authority held by the stakeholder", example = "BUDGET_APPROVAL")
        String authorityType,

        @Schema(description = "Current status of the approval authority", example = "ACTIVE")
        String status,

        @Schema(description = "Timestamp when the authority was assigned", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static ProjectApprovalAuthorityResponse from(ProjectApprovalAuthority e) {
        return new ProjectApprovalAuthorityResponse(e.id(), e.projectId(), e.stakeholderId(), e.authorityType(), e.status(), e.createdAt());
    }
}
