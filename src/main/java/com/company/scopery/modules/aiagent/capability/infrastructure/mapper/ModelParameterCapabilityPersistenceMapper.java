package com.company.scopery.modules.aiagent.capability.infrastructure.mapper;

import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterCapabilityStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterIfNullBehavior;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;
import com.company.scopery.modules.aiagent.capability.domain.valueobject.ModelParameterName;
import com.company.scopery.modules.aiagent.capability.infrastructure.persistence.entity.ModelParameterCapabilityJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ModelParameterCapabilityPersistenceMapper {

    public ModelParameterCapabilityJpaEntity toJpaEntity(ModelParameterCapability capability) {
        ModelParameterCapabilityJpaEntity entity = new ModelParameterCapabilityJpaEntity();
        entity.setId(capability.id());
        entity.setModelId(capability.modelId());
        entity.setParameterName(capability.parameterName().value());
        entity.setApiParameterKey(capability.apiParameterKey());
        entity.setSupportStatus(capability.supportStatus().name());
        entity.setValueType(capability.valueType().name());
        entity.setMinValue(capability.minValue());
        entity.setMaxValue(capability.maxValue());
        entity.setDefaultValue(capability.defaultValue());
        entity.setNullable(capability.nullable());
        entity.setIfNullBehavior(capability.ifNullBehavior() != null ? capability.ifNullBehavior().name() : null);
        entity.setDescription(capability.description());
        entity.setStatus(capability.status().name());
        if (capability.createdAt() != null) {
            entity.setCreatedAt(capability.createdAt());
        }
        return entity;
    }

    public ModelParameterCapability toDomain(ModelParameterCapabilityJpaEntity entity) {
        return ModelParameterCapability.reconstitute(
                entity.getId(),
                entity.getModelId(),
                ModelParameterName.of(entity.getParameterName()),
                entity.getApiParameterKey(),
                ModelParameterSupportStatus.valueOf(entity.getSupportStatus()),
                ModelParameterValueType.valueOf(entity.getValueType()),
                entity.getMinValue(),
                entity.getMaxValue(),
                entity.getDefaultValue(),
                entity.isNullable(),
                entity.getIfNullBehavior() != null
                        ? ModelParameterIfNullBehavior.valueOf(entity.getIfNullBehavior()) : null,
                entity.getDescription(),
                ModelParameterCapabilityStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
