package com.company.scopery.modules.resourcecapacity.effortestimate.http.request;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.util.UUID;
public record CreateEffortEstimateRequest(@NotBlank String targetType, @NotNull UUID targetId, @NotBlank String estimateType,
        @NotNull @DecimalMin("0") BigDecimal effortHours, UUID resourceRoleId, UUID resourceProfileId, String reason) {}
