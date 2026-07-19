package com.company.scopery.modules.quality.deployment.application.response;
import com.company.scopery.modules.quality.deployment.domain.model.DeploymentRecord;
import java.time.Instant; import java.util.UUID;
public record DeploymentRecordResponse(UUID id, UUID projectId, UUID releasePackageId, UUID deploymentEnvironmentId, String status,
                               Instant startedAt, Instant completedAt, Instant createdAt) {
    public static DeploymentRecordResponse from(DeploymentRecord e) {
        return new DeploymentRecordResponse(e.id(), e.projectId(), e.releasePackageId(), e.deploymentEnvironmentId(), e.status().name(),
                e.startedAt(), e.completedAt(), e.createdAt());
    }
}
