package com.company.scopery.modules.aiagent.eventconfig.infrastructure.mapper;

import com.company.scopery.modules.aiagent.eventconfig.domain.*;
import com.company.scopery.modules.aiagent.eventconfig.infrastructure.persistence.EventConfigJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EventConfigPersistenceMapper {

    public EventConfigJpaEntity toJpaEntity(EventConfig config) {
        EventConfigJpaEntity entity = new EventConfigJpaEntity();
        entity.setId(config.id());
        entity.setCode(config.code().value());
        entity.setName(config.name());
        entity.setEventDefinitionId(config.eventDefinitionId());
        entity.setEnvironment(config.environment().name());
        entity.setTriggerType(config.triggerType().name());
        entity.setAgentId(config.agentId());
        entity.setPromptVersionId(config.promptVersionId());
        entity.setModelDeploymentId(config.modelDeploymentId());
        entity.setConditionExpression(config.conditionExpression());
        entity.setDescription(config.description());
        entity.setStatus(config.status().name());
        if (config.createdAt() != null) {
            entity.setCreatedAt(config.createdAt());
        }
        return entity;
    }

    public EventConfig toDomain(EventConfigJpaEntity entity) {
        return EventConfig.reconstitute(
                entity.getId(),
                EventConfigCode.of(entity.getCode()),
                entity.getName(),
                entity.getEventDefinitionId(),
                EventConfigEnvironment.valueOf(entity.getEnvironment()),
                EventTriggerType.valueOf(entity.getTriggerType()),
                entity.getAgentId(),
                entity.getPromptVersionId(),
                entity.getModelDeploymentId(),
                entity.getConditionExpression(),
                entity.getDescription(),
                EventConfigStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
