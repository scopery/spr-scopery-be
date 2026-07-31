package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUseCaseConditionJpaRepository extends JpaRepository<UseCaseConditionJpaEntity, UUID> {
    Optional<UseCaseConditionJpaEntity> findByIdAndUseCaseId(UUID id, UUID useCaseId);
    @Query("SELECT c FROM UseCaseConditionJpaEntity c WHERE c.useCaseId = :useCaseId ORDER BY c.conditionType ASC, c.displayOrder ASC")
    List<UseCaseConditionJpaEntity> findByUseCaseIdOrderByConditionTypeAndDisplayOrder(@Param("useCaseId") UUID useCaseId);
    void deleteByIdAndUseCaseId(UUID id, UUID useCaseId);
}
