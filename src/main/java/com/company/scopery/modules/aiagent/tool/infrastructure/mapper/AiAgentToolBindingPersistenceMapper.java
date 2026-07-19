package com.company.scopery.modules.aiagent.tool.infrastructure.mapper;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiAgentToolBindingStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBinding;
import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiAgentToolBindingJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiAgentToolBindingPersistenceMapper {

    public AiAgentToolBindingJpaEntity toJpaEntity(AiAgentToolBinding binding) {
        AiAgentToolBindingJpaEntity entity = new AiAgentToolBindingJpaEntity();
        entity.setId(binding.id());
        entity.setAgentId(binding.agentId());
        entity.setToolId(binding.toolId());
        entity.setStatus(binding.status().name());
        if (binding.createdAt() != null) {
            entity.setCreatedAt(binding.createdAt());
        }
        return entity;
    }

    public AiAgentToolBinding toDomain(AiAgentToolBindingJpaEntity entity) {
        return AiAgentToolBinding.reconstitute(
                entity.getId(),
                entity.getAgentId(),
                entity.getToolId(),
                AiAgentToolBindingStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
