package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataRequirementUseCaseJpaRepository extends JpaRepository<RequirementUseCaseJpaEntity, RequirementUseCaseId> {

    @Query("SELECT e.id.useCaseId FROM RequirementUseCaseJpaEntity e WHERE e.id.requirementId = :requirementId")
    List<UUID> findUseCaseIdsByRequirementId(@Param("requirementId") UUID requirementId);

    @Query("SELECT e.id.requirementId FROM RequirementUseCaseJpaEntity e WHERE e.id.useCaseId = :useCaseId")
    List<UUID> findRequirementIdsByUseCaseId(@Param("useCaseId") UUID useCaseId);

    @Query("SELECT COUNT(e) > 0 FROM RequirementUseCaseJpaEntity e WHERE e.id.requirementId = :requirementId")
    boolean existsByRequirementId(@Param("requirementId") UUID requirementId);
}
