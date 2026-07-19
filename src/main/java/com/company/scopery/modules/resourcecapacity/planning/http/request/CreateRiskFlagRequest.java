package com.company.scopery.modules.resourcecapacity.planning.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateRiskFlagRequest(@NotBlank String riskReason, @NotBlank String impactType, String description, UUID resourceProfileId) {}
