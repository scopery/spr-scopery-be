package com.company.scopery.modules.trust.anonymization.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateAnonymizationPlanRequest(@NotNull UUID dataSubjectIndexId,
        @NotBlank String planJson, @NotBlank String reason) {}
