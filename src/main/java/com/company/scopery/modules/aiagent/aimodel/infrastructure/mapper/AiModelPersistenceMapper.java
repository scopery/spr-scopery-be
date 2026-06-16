package com.company.scopery.modules.aiagent.aimodel.infrastructure.mapper;

import com.company.scopery.modules.aiagent.aimodel.domain.*;
import com.company.scopery.modules.aiagent.aimodel.infrastructure.persistence.AiModelJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiModelPersistenceMapper {

    public AiModelJpaEntity toJpaEntity(AiModel model) {
        AiModelJpaEntity entity = new AiModelJpaEntity();
        entity.setId(model.id());
        entity.setProviderId(model.providerId());
        entity.setName(model.name());
        entity.setCode(model.code().value());
        entity.setProviderModelId(model.providerModelId());
        entity.setType(model.type().name());
        entity.setDescription(model.description());
        entity.setStatus(model.status().name());
        if (model.createdAt() != null) {
            entity.setCreatedAt(model.createdAt());
        }
        return entity;
    }

    public AiModel toDomain(AiModelJpaEntity entity) {
        return AiModel.reconstitute(
                entity.getId(),
                entity.getProviderId(),
                entity.getName(),
                AiModelCode.of(entity.getCode()),
                entity.getProviderModelId(),
                AiModelType.valueOf(entity.getType()),
                entity.getDescription(),
                AiModelStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}