package com.company.scopery.modules.quality.coverage.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateTestCaseCoverageRequest(@NotBlank String targetType, @NotNull UUID targetId, @NotBlank String coverageType) {}
