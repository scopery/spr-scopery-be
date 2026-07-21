package com.company.scopery.modules.aiaction.tool.infrastructure.mapper;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import com.company.scopery.modules.aiaction.tool.infrastructure.persistence.AiActionToolPolicyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionToolPolicyPersistenceMapper {

    public AiActionToolPolicyJpaEntity toJpaEntity(AiActionToolPolicy domain) {
        AiActionToolPolicyJpaEntity entity = new AiActionToolPolicyJpaEntity();
        entity.setId(domain.id());
        entity.setToolCode(domain.toolCode());
        entity.setToolVersion(domain.toolVersion());
        entity.setInvocationScope(domain.invocationScope().name());
        entity.setRiskLevel(domain.riskLevel().name());
        entity.setExecutionMode(domain.executionMode().name());
        entity.setMaxBatchTargets(domain.maxBatchTargets());
        entity.setDryRunRequired(domain.dryRunRequired());
        entity.setSupportsCompensation(domain.supportsCompensation());
        entity.setSupportsPause(domain.supportsPause());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public AiActionToolPolicy toDomain(AiActionToolPolicyJpaEntity entity) {
        return AiActionToolPolicy.reconstitute(
                entity.getId(),
                entity.getToolCode(),
                entity.getToolVersion(),
                AiActionInvocationScope.valueOf(entity.getInvocationScope()),
                AiActionRiskLevel.valueOf(entity.getRiskLevel()),
                AiActionExecutionMode.valueOf(entity.getExecutionMode()),
                entity.getMaxBatchTargets(),
                entity.isDryRunRequired(),
                entity.isSupportsCompensation(),
                entity.isSupportsPause(),
                AiActionToolPolicyStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}
