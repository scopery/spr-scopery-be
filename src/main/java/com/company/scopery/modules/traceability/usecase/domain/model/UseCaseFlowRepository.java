package com.company.scopery.modules.traceability.usecase.domain.model;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UseCaseFlowRepository {
    UseCaseFlow save(UseCaseFlow flow);
    Optional<UseCaseFlow> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    List<UseCaseFlow> findByUseCaseIdOrderByDisplayOrder(UUID useCaseId);
    boolean existsByUseCaseIdAndFlowType(UUID useCaseId, UseCaseFlowType flowType);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
