package com.company.scopery.modules.resourcecapacity.actualeffort.http.request;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record CreateActualEffortRequest(@NotBlank String targetType, @NotNull UUID targetId, @NotNull LocalDate effortDate,
        @NotNull @DecimalMin("0") BigDecimal effortHours, @NotBlank String inputMode, UUID resourceProfileId, String description) {}
