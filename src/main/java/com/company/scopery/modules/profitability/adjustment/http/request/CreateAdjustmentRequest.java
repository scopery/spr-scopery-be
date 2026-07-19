package com.company.scopery.modules.profitability.adjustment.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
public record CreateAdjustmentRequest(@NotBlank String adjustmentType, @NotNull BigDecimal amount, @NotBlank String reason) {}
