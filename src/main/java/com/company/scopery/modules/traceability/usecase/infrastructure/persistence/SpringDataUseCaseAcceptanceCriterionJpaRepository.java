package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUseCaseAcceptanceCriterionJpaRepository extends JpaRepository<UseCaseAcceptanceCriterionJpaEntity, UUID> {
    Optional<UseCaseAcceptanceCriterionJpaEntity> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    List<UseCaseAcceptanceCriterionJpaEntity> findByUseCaseIdOrderByDisplayOrderAsc(UUID useCaseId);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
