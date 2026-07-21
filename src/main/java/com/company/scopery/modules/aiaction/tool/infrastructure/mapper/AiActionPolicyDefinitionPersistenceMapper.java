package com.company.scopery.modules.aiaction.tool.infrastructure.mapper;

import com.company.scopery.modules.aiaction.tool.domain.model.AiActionPolicyDefinition;
import com.company.scopery.modules.aiaction.tool.infrastructure.persistence.AiActionPolicyDefinitionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionPolicyDefinitionPersistenceMapper {

    public AiActionPolicyDefinitionJpaEntity toJpaEntity(AiActionPolicyDefinition domain) {
        AiActionPolicyDefinitionJpaEntity entity = new AiActionPolicyDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setPolicyCode(domain.policyCode());
        entity.setPolicyVersion(domain.policyVersion());
        entity.setConfigJson(domain.configJson());
        entity.setStatus(domain.status());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public AiActionPolicyDefinition toDomain(AiActionPolicyDefinitionJpaEntity entity) {
        return AiActionPolicyDefinition.reconstitute(
                entity.getId(),
                entity.getPolicyCode(),
                entity.getPolicyVersion(),
                entity.getConfigJson(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
