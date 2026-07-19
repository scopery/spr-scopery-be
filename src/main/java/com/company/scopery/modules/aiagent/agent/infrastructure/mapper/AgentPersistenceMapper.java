package com.company.scopery.modules.aiagent.agent.infrastructure.mapper;

import com.company.scopery.modules.aiagent.agent.domain.enums.AgentAutonomyLevel;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentScope;
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
        entity.setAutonomyLevel(agent.autonomyLevel() != null
                ? agent.autonomyLevel().name() : AgentAutonomyLevel.SUGGEST_ONLY.name());
        entity.setScope(agent.scope() != null ? agent.scope().name() : AgentScope.SYSTEM.name());
        entity.setOrganizationId(agent.organizationId());
        entity.setWorkspaceId(agent.workspaceId());
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
                entity.getAutonomyLevel() != null
                        ? AgentAutonomyLevel.valueOf(entity.getAutonomyLevel())
                        : AgentAutonomyLevel.SUGGEST_ONLY,
                entity.getScope() != null ? AgentScope.valueOf(entity.getScope()) : AgentScope.SYSTEM,
                entity.getOrganizationId(),
                entity.getWorkspaceId(),
                AgentStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
