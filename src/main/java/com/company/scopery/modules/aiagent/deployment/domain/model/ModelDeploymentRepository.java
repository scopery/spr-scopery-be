package com.company.scopery.modules.aiagent.deployment.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.valueobject.ModelDeploymentCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModelDeploymentRepository {

    ModelDeployment save(ModelDeployment deployment);

    Optional<ModelDeployment> findById(UUID id);

    boolean existsByModelIdAndCode(UUID modelId, ModelDeploymentCode code);

    PageResult<ModelDeployment> findAll(UUID modelId, ModelDeploymentEnvironment environment,
                                         String keyword, ModelDeploymentStatus status,
                                         Boolean isDefault, PageQuery pageQuery);

    List<ModelDeployment> findAllByStatus(ModelDeploymentStatus status);

    /**
     * Unsets is_default for deployments under the same model and environment.
     * Pass null as excludeId to clear ALL defaults (used during create).
     * Pass a non-null excludeId to clear others only (used during update/set-default).
     * Returns the number of rows affected.
     */
    int clearDefaultFlags(UUID modelId, ModelDeploymentEnvironment environment, UUID excludeId);
}
