package com.company.scopery.modules.profitability.profile.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateProfitabilityProfileRequest(@NotBlank String currency) {}
