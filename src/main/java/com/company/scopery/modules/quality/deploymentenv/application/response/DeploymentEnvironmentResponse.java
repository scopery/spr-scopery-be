package com.company.scopery.modules.quality.deploymentenv.application.response;
import com.company.scopery.modules.quality.deploymentenv.domain.model.DeploymentEnvironment;
import java.time.Instant; import java.util.UUID;
public record DeploymentEnvironmentResponse(UUID id, UUID workspaceId, UUID projectId, String code, String name, String environmentType, boolean active, Instant createdAt) {
    public static DeploymentEnvironmentResponse from(DeploymentEnvironment e) {
        return new DeploymentEnvironmentResponse(e.id(), e.workspaceId(), e.projectId(), e.code(), e.name(), e.environmentType().name(), e.active(), e.createdAt());
    }
}
