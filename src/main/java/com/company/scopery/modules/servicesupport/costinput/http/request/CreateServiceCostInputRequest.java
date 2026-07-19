package com.company.scopery.modules.servicesupport.costinput.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal; import java.util.UUID;
public record CreateServiceCostInputRequest(UUID supportCaseId, @NotBlank String sourceType,
        @NotNull BigDecimal costAmount, String currency) {}
