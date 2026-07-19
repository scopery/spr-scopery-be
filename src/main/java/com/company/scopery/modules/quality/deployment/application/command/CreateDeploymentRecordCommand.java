package com.company.scopery.modules.quality.deployment.application.command;
import java.util.UUID;
public record CreateDeploymentRecordCommand(UUID projectId, UUID releasePackageId, UUID deploymentEnvironmentId, String buildReference, String deploymentReference, UUID rollbackPlanId) {}
