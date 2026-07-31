package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UseCaseAcceptanceCriterionRepository {
    UseCaseAcceptanceCriterion save(UseCaseAcceptanceCriterion criterion);
    Optional<UseCaseAcceptanceCriterion> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    List<UseCaseAcceptanceCriterion> findByUseCaseIdOrderByDisplayOrder(UUID useCaseId);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
