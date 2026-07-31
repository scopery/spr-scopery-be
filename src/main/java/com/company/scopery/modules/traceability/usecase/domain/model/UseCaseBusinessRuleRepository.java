package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UseCaseBusinessRuleRepository {
    UseCaseBusinessRule save(UseCaseBusinessRule rule);
    Optional<UseCaseBusinessRule> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    List<UseCaseBusinessRule> findByUseCaseIdOrderByDisplayOrder(UUID useCaseId);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
