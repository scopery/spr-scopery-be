package com.company.scopery.modules.quality.deploymentenv.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDeploymentEnvironmentJpaRepository extends JpaRepository<DeploymentEnvironmentJpaEntity, UUID> {
    Optional<DeploymentEnvironmentJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DeploymentEnvironmentJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
