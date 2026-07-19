package com.company.scopery.modules.quality.deploymentenv.domain.model;
import com.company.scopery.modules.quality.deploymentenv.domain.enums.EnvironmentType;
import java.time.Instant; import java.util.UUID;
public record DeploymentEnvironment(UUID id, UUID workspaceId, UUID projectId, String code, String name, EnvironmentType environmentType,
                       String description, boolean active, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static DeploymentEnvironment create(UUID workspaceId, UUID projectId, String code, String name, EnvironmentType type, String description) {
        Instant now = Instant.now();
        return new DeploymentEnvironment(UUID.randomUUID(), workspaceId, projectId, code, name, type, description, true, null, null, 0, now, now);
    }
    public DeploymentEnvironment archive(UUID actorId) {
        return new DeploymentEnvironment(id, workspaceId, projectId, code, name, environmentType, description, false, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
