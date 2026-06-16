package com.company.scopery.modules.aiagent.deployment.infrastructure.mapper;

import com.company.scopery.modules.aiagent.deployment.domain.*;
import com.company.scopery.modules.aiagent.deployment.infrastructure.persistence.ModelDeploymentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ModelDeploymentPersistenceMapper {

    public ModelDeploymentJpaEntity toJpaEntity(ModelDeployment deployment) {
        ModelDeploymentJpaEntity entity = new ModelDeploymentJpaEntity();
        entity.setId(deployment.id());
        entity.setModelId(deployment.modelId());
        entity.setName(deployment.name());
        entity.setCode(deployment.code().value());
        entity.setEnvironment(deployment.environment().name());
        entity.setProviderDeploymentId(deployment.providerDeploymentId());
        entity.setEndpointUrl(deployment.endpointUrl());
        entity.setDefaultTemperature(deployment.defaultTemperature());
        entity.setDefaultMaxOutputTokens(deployment.defaultMaxOutputTokens());
        entity.setDefault(deployment.isDefault());
        entity.setDescription(deployment.description());
        entity.setStatus(deployment.status().name());
        if (deployment.createdAt() != null) {
            entity.setCreatedAt(deployment.createdAt());
        }
        return entity;
    }

    public ModelDeployment toDomain(ModelDeploymentJpaEntity entity) {
        return ModelDeployment.reconstitute(
                entity.getId(),
                entity.getModelId(),
                entity.getName(),
                ModelDeploymentCode.of(entity.getCode()),
                ModelDeploymentEnvironment.valueOf(entity.getEnvironment()),
                entity.getProviderDeploymentId(),
                entity.getEndpointUrl(),
                entity.getDefaultTemperature(),
                entity.getDefaultMaxOutputTokens(),
                entity.isDefault(),
                entity.getDescription(),
                ModelDeploymentStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}