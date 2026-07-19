package com.company.scopery.modules.ratecard.ratecardline.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request body for creating a new rate card line")
public record CreateRateCardLineRequest(
        @Schema(description = "Cost role associated with this rate card line", example = "550e8400-e29b-41d4-a716-446655440004")
        @NotNull UUID costRoleId,

        @Schema(description = "Seniority level qualifier for the rate (e.g. Junior, Senior)", example = "Senior", nullable = true)
        String seniorityLevel,

        @Schema(description = "Location code qualifier for the rate (e.g. US-NYC, APAC)", example = "US-NYC", nullable = true)
        String locationCode,

        @Schema(description = "Currency code for the rates on this line following ISO 4217", example = "USD")
        @NotBlank String currencyCode,

        @Schema(description = "Internal cost rate per hour in the specified currency", example = "80.00")
        @NotNull BigDecimal costRatePerHour,

        @Schema(description = "Billing rate per hour charged to the client in the specified currency", example = "150.00", nullable = true)
        BigDecimal billingRatePerHour,

        @Schema(description = "Optional notes or comments for this rate card line", example = "Includes benefits overhead", nullable = true)
        String notes
) {}
