package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUseCaseJpaRepository extends JpaRepository<UseCaseJpaEntity, UUID> {
    Optional<UseCaseJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    boolean existsByProjectIdAndKey(UUID projectId, String key);
    List<UseCaseJpaEntity> findByProjectId(UUID projectId);
    List<UseCaseJpaEntity> findByPrimaryFunctionId(UUID primaryFunctionId);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
}
