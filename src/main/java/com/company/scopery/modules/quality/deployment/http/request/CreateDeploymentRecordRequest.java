package com.company.scopery.modules.quality.deployment.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateDeploymentRecordRequest(@NotNull UUID releasePackageId, @NotNull UUID deploymentEnvironmentId, String buildReference, String deploymentReference, UUID rollbackPlanId) {}
