package com.company.scopery.modules.aicontext.policy.infrastructure.mapper;

import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicy;
import com.company.scopery.modules.aicontext.policy.infrastructure.persistence.AiContextResolutionPolicyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiContextResolutionPolicyPersistenceMapper {

    public AiContextResolutionPolicyJpaEntity toJpaEntity(AiContextResolutionPolicy policy) {
        AiContextResolutionPolicyJpaEntity entity = new AiContextResolutionPolicyJpaEntity();
        entity.setId(policy.id());
        entity.setWorkspaceId(policy.workspaceId());
        entity.setPolicyCode(policy.policyCode());
        entity.setLabel(policy.label());
        entity.setMaxTokens(policy.maxTokens());
        entity.setIncludeRelated(policy.includeRelated());
        entity.setEnabled(policy.enabled());
        // Set createdAt so Spring Data's isNew() check (via @CreatedDate null-detection) returns false,
        // ensuring merge() is used for both INSERT and UPDATE consistently.
        if (policy.createdAt() != null) {
            entity.setCreatedAt(policy.createdAt());
        }
        return entity;
    }

    public AiContextResolutionPolicy toDomain(AiContextResolutionPolicyJpaEntity entity) {
        return new AiContextResolutionPolicy(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getPolicyCode(),
                entity.getLabel(),
                entity.getMaxTokens(),
                entity.isIncludeRelated(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
