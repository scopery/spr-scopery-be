package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataUseCaseSupFnJpaRepository extends JpaRepository<UseCaseSupFnJpaEntity, UseCaseSupFnId> {

    @Query("SELECT e.id.functionId FROM UseCaseSupFnJpaEntity e WHERE e.id.useCaseId = :useCaseId")
    List<UUID> findFunctionIdsByUseCaseId(@Param("useCaseId") UUID useCaseId);

    @Query("SELECT e.id.useCaseId FROM UseCaseSupFnJpaEntity e WHERE e.id.functionId = :functionId")
    List<UUID> findUseCaseIdsByFunctionId(@Param("functionId") UUID functionId);
}
