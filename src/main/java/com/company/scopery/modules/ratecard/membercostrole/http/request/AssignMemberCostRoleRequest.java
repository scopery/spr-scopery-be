package com.company.scopery.modules.ratecard.membercostrole.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request body for assigning a cost role to a workspace member")
public record AssignMemberCostRoleRequest(
        @Schema(description = "Workspace the assignment belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull UUID workspaceId,

        @Schema(description = "Workspace member to assign the cost role to", example = "550e8400-e29b-41d4-a716-446655440002")
        @NotNull UUID workspaceMemberId,

        @Schema(description = "Cost role to assign to the workspace member", example = "550e8400-e29b-41d4-a716-446655440004")
        @NotNull UUID costRoleId,

        @Schema(description = "Whether this should be the default cost role for the workspace member (null treated as false)", example = "true", nullable = true)
        Boolean isDefault,

        @Schema(description = "Date from which this assignment is effective", example = "2026-01-01")
        @NotNull LocalDate effectiveFrom,

        @Schema(description = "Date on which this assignment expires (null if open-ended)", example = "2026-12-31", nullable = true)
        LocalDate effectiveTo
) {}
