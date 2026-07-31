package com.company.scopery.modules.traceability.usecase.infrastructure.mapper;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseBusinessRule;
import com.company.scopery.modules.traceability.usecase.infrastructure.persistence.UseCaseBusinessRuleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UseCaseBusinessRulePersistenceMapper {

    public UseCaseBusinessRule toDomain(UseCaseBusinessRuleJpaEntity e) {
        return new UseCaseBusinessRule(
                e.getId(),
                e.getUseCaseId(),
                e.getRuleCode(),
                e.getDescription(),
                e.getDisplayOrder(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public UseCaseBusinessRuleJpaEntity toJpaEntity(UseCaseBusinessRule d) {
        UseCaseBusinessRuleJpaEntity e = new UseCaseBusinessRuleJpaEntity();
        e.setId(d.id());
        e.setUseCaseId(d.useCaseId());
        e.setRuleCode(d.ruleCode());
        e.setDescription(d.description());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
