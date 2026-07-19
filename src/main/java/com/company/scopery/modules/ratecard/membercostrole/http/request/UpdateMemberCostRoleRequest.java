package com.company.scopery.modules.ratecard.membercostrole.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request body for updating a workspace member cost role assignment")
public record UpdateMemberCostRoleRequest(
        @Schema(description = "Updated cost role to assign to the workspace member", example = "550e8400-e29b-41d4-a716-446655440004")
        @NotNull UUID costRoleId,

        @Schema(description = "Whether this should be the default cost role for the workspace member (null treated as false)", example = "true", nullable = true)
        Boolean isDefault,

        @Schema(description = "Updated effective start date of the assignment", example = "2026-01-01")
        @NotNull LocalDate effectiveFrom,

        @Schema(description = "Updated expiry date of the assignment (null if open-ended)", example = "2026-12-31", nullable = true)
        LocalDate effectiveTo
) {}
