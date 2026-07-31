package com.company.scopery.modules.traceability.usecase.infrastructure.mapper;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowType;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlow;
import com.company.scopery.modules.traceability.usecase.infrastructure.persistence.UseCaseFlowJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UseCaseFlowPersistenceMapper {

    public UseCaseFlow toDomain(UseCaseFlowJpaEntity e) {
        return new UseCaseFlow(
                e.getId(),
                e.getUseCaseId(),
                e.getFlowType() != null ? UseCaseFlowType.valueOf(e.getFlowType()) : UseCaseFlowType.MAIN,
                e.getName(),
                e.getSourceStepId(),
                e.getConditionText(),
                e.getDisplayOrder(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public UseCaseFlowJpaEntity toJpaEntity(UseCaseFlow d) {
        UseCaseFlowJpaEntity e = new UseCaseFlowJpaEntity();
        e.setId(d.id());
        e.setUseCaseId(d.useCaseId());
        e.setFlowType(d.flowType().name());
        e.setName(d.name());
        e.setSourceStepId(d.sourceStepId());
        e.setConditionText(d.conditionText());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
