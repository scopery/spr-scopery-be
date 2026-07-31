package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UseCaseConditionRepository {
    UseCaseCondition save(UseCaseCondition condition);
    Optional<UseCaseCondition> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    List<UseCaseCondition> findByUseCaseIdOrderByConditionTypeAndDisplayOrder(UUID useCaseId);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
