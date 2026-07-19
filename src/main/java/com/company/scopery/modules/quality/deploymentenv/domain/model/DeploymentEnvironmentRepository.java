package com.company.scopery.modules.quality.deploymentenv.domain.model;
import java.util.*;
public interface DeploymentEnvironmentRepository {
    DeploymentEnvironment save(DeploymentEnvironment entity);
    Optional<DeploymentEnvironment> findByIdAndProjectId(UUID id, UUID projectId);
    List<DeploymentEnvironment> findByProjectId(UUID projectId);
}
