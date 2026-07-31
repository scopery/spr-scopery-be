package com.company.scopery.modules.traceability.usecase.infrastructure.mapper;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterion;
import com.company.scopery.modules.traceability.usecase.infrastructure.persistence.UseCaseAcceptanceCriterionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UseCaseAcceptanceCriterionPersistenceMapper {

    public UseCaseAcceptanceCriterion toDomain(UseCaseAcceptanceCriterionJpaEntity e) {
        return new UseCaseAcceptanceCriterion(
                e.getId(),
                e.getUseCaseId(),
                e.getTitle(),
                e.getGivenText(),
                e.getWhenText(),
                e.getThenText(),
                e.getDisplayOrder(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public UseCaseAcceptanceCriterionJpaEntity toJpaEntity(UseCaseAcceptanceCriterion d) {
        UseCaseAcceptanceCriterionJpaEntity e = new UseCaseAcceptanceCriterionJpaEntity();
        e.setId(d.id());
        e.setUseCaseId(d.useCaseId());
        e.setTitle(d.title());
        e.setGivenText(d.givenText());
        e.setWhenText(d.whenText());
        e.setThenText(d.thenText());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
