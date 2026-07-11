package com.company.scopery.modules.aiagent.usagepolicy.infrastructure.mapper;

import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;
import com.company.scopery.modules.aiagent.usagepolicy.infrastructure.persistence.entity.UsagePolicyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UsagePolicyPersistenceMapper {

    public UsagePolicyJpaEntity toJpaEntity(UsagePolicy policy) {
        UsagePolicyJpaEntity entity = new UsagePolicyJpaEntity();
        entity.setId(policy.id());
        entity.setCode(policy.code().value());
        entity.setName(policy.name());
        entity.setTargetType(policy.targetType().name());
        entity.setTargetId(policy.targetId());
        entity.setMaxRequestsPerPeriod(policy.maxRequestsPerPeriod());
        entity.setMaxTokensPerPeriod(policy.maxTokensPerPeriod());
        entity.setMaxCostPerPeriod(policy.maxCostPerPeriod());
        entity.setMaxConcurrentRequests(policy.maxConcurrentRequests());
        entity.setDailyBudget(policy.dailyBudget());
        entity.setPeriod(policy.period() != null ? policy.period().name() : null);
        entity.setAction(policy.action().name());
        entity.setPriority(policy.priority());
        entity.setDescription(policy.description());
        entity.setStatus(policy.status().name());
        if (policy.createdAt() != null) {
            entity.setCreatedAt(policy.createdAt());
        }
        return entity;
    }

    public UsagePolicy toDomain(UsagePolicyJpaEntity entity) {
        return UsagePolicy.reconstitute(
                entity.getId(),
                UsagePolicyCode.of(entity.getCode()),
                entity.getName(),
                UsagePolicyTargetType.valueOf(entity.getTargetType()),
                entity.getTargetId(),
                entity.getMaxRequestsPerPeriod(),
                entity.getMaxTokensPerPeriod(),
                entity.getMaxCostPerPeriod(),
                entity.getMaxConcurrentRequests(),
                entity.getDailyBudget(),
                entity.getPeriod() != null ? UsagePolicyPeriod.valueOf(entity.getPeriod()) : null,
                UsagePolicyAction.valueOf(entity.getAction()),
                entity.getPriority(),
                entity.getDescription(),
                UsagePolicyStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
