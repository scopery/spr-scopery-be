package com.company.scopery.modules.quality.deployment.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDeploymentRecordJpaRepository extends JpaRepository<DeploymentRecordJpaEntity, UUID> {
    Optional<DeploymentRecordJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DeploymentRecordJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
