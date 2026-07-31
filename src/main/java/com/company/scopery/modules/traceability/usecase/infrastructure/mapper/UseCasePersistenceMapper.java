package com.company.scopery.modules.traceability.usecase.infrastructure.mapper;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseCompletenessStatus;
import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseStatus;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCase;
import com.company.scopery.modules.traceability.usecase.infrastructure.persistence.UseCaseJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UseCasePersistenceMapper {

    public UseCase toDomain(UseCaseJpaEntity e) {
        return new UseCase(
                e.getId(),
                e.getProjectId(),
                e.getPrimaryFunctionId(),
                e.getKey(),
                e.getName(),
                e.getGoal(),
                e.getPrimaryActorName(),
                e.getTriggerText(),
                e.getStatus() != null ? UseCaseStatus.valueOf(e.getStatus()) : UseCaseStatus.DRAFT,
                e.getCompletenessStatus() != null ? UseCaseCompletenessStatus.valueOf(e.getCompletenessStatus()) : UseCaseCompletenessStatus.NOT_STARTED,
                e.getDisplayOrder(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public UseCaseJpaEntity toJpaEntity(UseCase d) {
        UseCaseJpaEntity e = new UseCaseJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setPrimaryFunctionId(d.primaryFunctionId());
        e.setKey(d.key());
        e.setName(d.name());
        e.setGoal(d.goal());
        e.setPrimaryActorName(d.primaryActorName());
        e.setTriggerText(d.triggerText());
        e.setStatus(d.status().name());
        e.setCompletenessStatus(d.completenessStatus().name());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
            e.setVersion(d.version());
        }
        return e;
    }
}
