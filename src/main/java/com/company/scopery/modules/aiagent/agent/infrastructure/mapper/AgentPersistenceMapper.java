package com.company.scopery.modules.aiagent.agent.infrastructure.mapper;

import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;
import com.company.scopery.modules.aiagent.agent.infrastructure.persistence.entity.AgentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AgentPersistenceMapper {

    public AgentJpaEntity toJpaEntity(Agent agent) {
        AgentJpaEntity entity = new AgentJpaEntity();
        entity.setId(agent.id());
        entity.setName(agent.name());
        entity.setCode(agent.code().value());
        entity.setType(agent.type().name());
        entity.setDescription(agent.description());
        entity.setDefaultModelDeploymentId(agent.defaultModelDeploymentId());
        entity.setOutputFormat(agent.outputFormat() != null ? agent.outputFormat().name() : null);
        entity.setStatus(agent.status().name());
        if (agent.createdAt() != null) {
            entity.setCreatedAt(agent.createdAt());
        }
        return entity;
    }

    public Agent toDomain(AgentJpaEntity entity) {
        return Agent.reconstitute(
                entity.getId(),
                entity.getName(),
                AgentCode.of(entity.getCode()),
                AgentType.valueOf(entity.getType()),
                entity.getDescription(),
                entity.getDefaultModelDeploymentId(),
                entity.getOutputFormat() != null ? AgentOutputFormat.valueOf(entity.getOutputFormat()) : null,
                AgentStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}