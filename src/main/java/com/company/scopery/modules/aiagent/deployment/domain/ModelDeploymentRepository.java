package com.company.scopery.modules.aiagent.deployment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModelDeploymentRepository {

    ModelDeployment save(ModelDeployment deployment);

    Optional<ModelDeployment> findById(UUID id);

    boolean existsByModelIdAndCode(UUID modelId, ModelDeploymentCode code);

    Page<ModelDeployment> findAll(UUID modelId, ModelDeploymentEnvironment environment,
                                   String keyword, ModelDeploymentStatus status,
                                   Boolean isDefault, Pageable pageable);

    List<ModelDeployment> findAllByStatus(ModelDeploymentStatus status);

    /**
     * Unsets is_default for deployments under the same model and environment.
     * Pass null as excludeId to clear ALL defaults (used during create).
     * Pass a non-null excludeId to clear others only (used during update/set-default).
     * Returns the number of rows affected.
     */
    int clearDefaultFlags(UUID modelId, ModelDeploymentEnvironment environment, UUID excludeId);
}