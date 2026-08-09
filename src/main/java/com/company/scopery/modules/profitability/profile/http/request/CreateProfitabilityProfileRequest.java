package com.company.scopery.modules.profitability.profile.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateProfitabilityProfileRequest(
        @NotNull UUID workspaceId,
        @NotBlank String currency
) {}
