package com.company.scopery.modules.quality.testplan.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateTestPlanRequest(@NotBlank String name, String code, String description, @NotBlank String testLevel, UUID qualityPlanId, UUID releasePackageId) {}
