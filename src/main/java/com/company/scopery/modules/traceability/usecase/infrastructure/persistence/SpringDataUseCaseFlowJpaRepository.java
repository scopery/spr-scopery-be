package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUseCaseFlowJpaRepository extends JpaRepository<UseCaseFlowJpaEntity, UUID> {
    Optional<UseCaseFlowJpaEntity> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    List<UseCaseFlowJpaEntity> findByUseCaseIdOrderByDisplayOrderAsc(UUID useCaseId);
    boolean existsByUseCaseIdAndFlowType(UUID useCaseId, String flowType);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
