package com.company.scopery.modules.traceability.usecase.infrastructure.mapper;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowStepType;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStep;
import com.company.scopery.modules.traceability.usecase.infrastructure.persistence.UseCaseFlowStepJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UseCaseFlowStepPersistenceMapper {

    public UseCaseFlowStep toDomain(UseCaseFlowStepJpaEntity e) {
        return new UseCaseFlowStep(
                e.getId(),
                e.getFlowId(),
                e.getStepType() != null ? UseCaseFlowStepType.valueOf(e.getStepType()) : UseCaseFlowStepType.USER_ACTION,
                e.getScreenContextId(),
                e.getContentJson(),
                e.getNextScreenId(),
                e.getDisplayOrder(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public UseCaseFlowStepJpaEntity toJpaEntity(UseCaseFlowStep d) {
        UseCaseFlowStepJpaEntity e = new UseCaseFlowStepJpaEntity();
        e.setId(d.id());
        e.setFlowId(d.flowId());
        e.setStepType(d.stepType().name());
        e.setScreenContextId(d.screenContextId());
        e.setContentJson(d.contentJson());
        e.setNextScreenId(d.nextScreenId());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
