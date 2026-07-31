package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUseCaseFlowStepJpaRepository extends JpaRepository<UseCaseFlowStepJpaEntity, UUID> {
    Optional<UseCaseFlowStepJpaEntity> findByIdAndFlowId(UUID id, UUID flowId);
    List<UseCaseFlowStepJpaEntity> findByFlowIdOrderByDisplayOrderAsc(UUID flowId);
    void deleteByIdAndFlowId(UUID id, UUID flowId);
}
