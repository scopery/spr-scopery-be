package com.company.scopery.modules.aiaction.plan.infrastructure.mapper;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionPlanStatus;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.infrastructure.persistence.AiActionPlanJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionPlanPersistenceMapper {

    public AiActionPlanJpaEntity toJpaEntity(AiActionPlan domain) {
        AiActionPlanJpaEntity entity = new AiActionPlanJpaEntity();
        entity.setId(domain.id());
        entity.setRequestId(domain.requestId());
        entity.setPlanNumber(domain.planNumber());
        entity.setStatus(domain.status().name());
        entity.setPolicyCode(domain.policyCode());
        entity.setPolicyVersion(domain.policyVersion());
        entity.setPlanHash(domain.planHash());
        entity.setContextHash(domain.contextHash());
        entity.setSourceStateHash(domain.sourceStateHash());
        entity.setRiskLevel(domain.riskLevel() != null ? domain.riskLevel().name() : null);
        entity.setExecutionMode(domain.executionMode() != null ? domain.executionMode().name() : null);
        entity.setRequiresConfirmation(domain.requiresConfirmation());
        entity.setStepCount(domain.stepCount());
        entity.setTargetCount(domain.targetCount());
        entity.setSummary(domain.summary());
        entity.setVersion(domain.version());
        entity.setExpiresAt(domain.expiresAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }

    public AiActionPlan toDomain(AiActionPlanJpaEntity entity) {
        return AiActionPlan.reconstitute(
                entity.getId(),
                entity.getRequestId(),
                entity.getPlanNumber(),
                AiActionPlanStatus.valueOf(entity.getStatus()),
                entity.getPolicyCode(),
                entity.getPolicyVersion(),
                entity.getPlanHash(),
                entity.getContextHash(),
                entity.getSourceStateHash(),
                entity.getRiskLevel() != null ? AiActionRiskLevel.valueOf(entity.getRiskLevel()) : null,
                entity.getExecutionMode() != null ? AiActionExecutionMode.valueOf(entity.getExecutionMode()) : null,
                entity.isRequiresConfirmation(),
                entity.getStepCount(),
                entity.getTargetCount(),
                entity.getSummary(),
                entity.getVersion(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
