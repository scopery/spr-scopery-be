package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UseCaseFlowStepRepository {
    UseCaseFlowStep save(UseCaseFlowStep step);
    List<UseCaseFlowStep> saveAll(List<UseCaseFlowStep> steps);
    Optional<UseCaseFlowStep> findByIdAndFlowId(UUID id, UUID flowId);
    List<UseCaseFlowStep> findByFlowIdOrderByDisplayOrder(UUID flowId);
    void deleteByIdAndFlowId(UUID id, UUID flowId);
}
