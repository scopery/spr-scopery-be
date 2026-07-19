package com.company.scopery.modules.quality.testrun.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateTestRunRequest(@NotBlank String name, @NotBlank String runType, UUID testPlanId, UUID testSuiteId, UUID releasePackageId) {}
