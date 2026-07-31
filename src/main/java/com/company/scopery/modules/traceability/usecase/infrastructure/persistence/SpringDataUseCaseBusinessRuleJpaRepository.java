package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUseCaseBusinessRuleJpaRepository extends JpaRepository<UseCaseBusinessRuleJpaEntity, UUID> {
    Optional<UseCaseBusinessRuleJpaEntity> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    List<UseCaseBusinessRuleJpaEntity> findByUseCaseIdOrderByDisplayOrderAsc(UUID useCaseId);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
