package com.company.scopery.modules.quality.deployment.domain.model;
import com.company.scopery.modules.quality.deployment.domain.enums.DeploymentStatus;
import java.time.Instant; import java.util.UUID;
public record DeploymentRecord(UUID id, UUID projectId, UUID workspaceId, UUID releasePackageId, UUID deploymentEnvironmentId,
                       DeploymentStatus status, String buildReference, String deploymentReference,
                       Instant startedAt, Instant completedAt, UUID deployedBy, String failureReason,
                       UUID rollbackPlanId, Instant rolledBackAt, UUID rolledBackBy, String rollbackReason,
                       String traceId, int version, Instant createdAt, Instant updatedAt) {
    public static DeploymentRecord create(UUID projectId, UUID workspaceId, UUID releasePackageId, UUID envId, String buildReference, String deploymentReference, UUID rollbackPlanId) {
        Instant now = Instant.now();
        return new DeploymentRecord(UUID.randomUUID(), projectId, workspaceId, releasePackageId, envId, DeploymentStatus.PLANNED,
                buildReference, deploymentReference, null, null, null, null, rollbackPlanId, null, null, null, null, 0, now, now);
    }
    public DeploymentRecord start(UUID actorId) {
        return new DeploymentRecord(id, projectId, workspaceId, releasePackageId, deploymentEnvironmentId, DeploymentStatus.IN_PROGRESS,
                buildReference, deploymentReference, Instant.now(), null, actorId, null, rollbackPlanId, null, null, null, traceId, version, createdAt, Instant.now());
    }
    public DeploymentRecord succeed() {
        return new DeploymentRecord(id, projectId, workspaceId, releasePackageId, deploymentEnvironmentId, DeploymentStatus.SUCCEEDED,
                buildReference, deploymentReference, startedAt, Instant.now(), deployedBy, null, rollbackPlanId, null, null, null, traceId, version, createdAt, Instant.now());
    }
    public DeploymentRecord fail(String reason) {
        return new DeploymentRecord(id, projectId, workspaceId, releasePackageId, deploymentEnvironmentId, DeploymentStatus.FAILED,
                buildReference, deploymentReference, startedAt, Instant.now(), deployedBy, reason, rollbackPlanId, null, null, null, traceId, version, createdAt, Instant.now());
    }
    public DeploymentRecord rollback(UUID actorId, String reason) {
        return new DeploymentRecord(id, projectId, workspaceId, releasePackageId, deploymentEnvironmentId, DeploymentStatus.ROLLED_BACK,
                buildReference, deploymentReference, startedAt, completedAt, deployedBy, failureReason, rollbackPlanId, Instant.now(), actorId, reason, traceId, version, createdAt, Instant.now());
    }
}
