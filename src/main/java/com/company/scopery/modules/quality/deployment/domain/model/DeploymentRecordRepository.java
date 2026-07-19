package com.company.scopery.modules.quality.deployment.domain.model;
import java.util.*;
public interface DeploymentRecordRepository {
    DeploymentRecord save(DeploymentRecord entity);
    Optional<DeploymentRecord> findByIdAndProjectId(UUID id, UUID projectId);
    List<DeploymentRecord> findByProjectId(UUID projectId);
}
