package com.company.scopery.modules.aiassistant.workspaceconfig.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfig;
import com.company.scopery.modules.aiassistant.workspaceconfig.infrastructure.persistence.AiAssistantWorkspaceConfigJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiAssistantWorkspaceConfigPersistenceMapper {

    public AiAssistantWorkspaceConfigJpaEntity toJpaEntity(AiAssistantWorkspaceConfig domain) {
        AiAssistantWorkspaceConfigJpaEntity entity = new AiAssistantWorkspaceConfigJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setModelDeploymentId(domain.modelDeploymentId());
        entity.setModelProvider(domain.modelProvider());
        entity.setModelName(domain.modelName());
        entity.setSystemPromptOverride(domain.systemPromptOverride());
        entity.setTemperatureOverride(domain.temperatureOverride());
        entity.setMaxOutputTokensOverride(domain.maxOutputTokensOverride());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }

    public AiAssistantWorkspaceConfig toDomain(AiAssistantWorkspaceConfigJpaEntity entity) {
        return AiAssistantWorkspaceConfig.reconstitute(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getModelDeploymentId(),
                entity.getModelProvider(),
                entity.getModelName(),
                entity.getSystemPromptOverride(),
                entity.getTemperatureOverride(),
                entity.getMaxOutputTokensOverride(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
