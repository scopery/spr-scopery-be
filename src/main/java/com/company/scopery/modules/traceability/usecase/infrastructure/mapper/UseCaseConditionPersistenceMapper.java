package com.company.scopery.modules.traceability.usecase.infrastructure.mapper;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseConditionType;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseCondition;
import com.company.scopery.modules.traceability.usecase.infrastructure.persistence.UseCaseConditionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UseCaseConditionPersistenceMapper {

    public UseCaseCondition toDomain(UseCaseConditionJpaEntity e) {
        return new UseCaseCondition(
                e.getId(),
                e.getUseCaseId(),
                e.getConditionType() != null ? UseCaseConditionType.valueOf(e.getConditionType()) : UseCaseConditionType.PRECONDITION,
                e.getContent(),
                e.getDisplayOrder(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public UseCaseConditionJpaEntity toJpaEntity(UseCaseCondition d) {
        UseCaseConditionJpaEntity e = new UseCaseConditionJpaEntity();
        e.setId(d.id());
        e.setUseCaseId(d.useCaseId());
        e.setConditionType(d.conditionType().name());
        e.setContent(d.content());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
