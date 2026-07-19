package com.company.scopery.modules.externalparty.authority.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for designating a project approval authority")
public record CreateProjectApprovalAuthorityRequest(
        @Schema(description = "ID of the project stakeholder who holds the approval authority", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID stakeholderId,

        @Schema(description = "Type of authority the stakeholder holds (e.g. BUDGET_APPROVAL, SCOPE_APPROVAL)", example = "BUDGET_APPROVAL")
        @NotBlank String authorityType,

        @Schema(description = "Additional notes about the approval authority", example = "Approves all budget items above $10,000", nullable = true)
        String notes) {}
