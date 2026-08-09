package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataRequirementFunctionJpaRepository extends JpaRepository<RequirementFunctionJpaEntity, RequirementFunctionId> {

    @Query("SELECT e.id.functionId FROM RequirementFunctionJpaEntity e WHERE e.id.requirementId = :requirementId")
    List<UUID> findFunctionIdsByRequirementId(@Param("requirementId") UUID requirementId);

    @Query("SELECT e.id.requirementId FROM RequirementFunctionJpaEntity e WHERE e.id.functionId = :functionId")
    List<UUID> findRequirementIdsByFunctionId(@Param("functionId") UUID functionId);

    @Query("SELECT COUNT(e) > 0 FROM RequirementFunctionJpaEntity e WHERE e.id.requirementId = :requirementId")
    boolean existsByRequirementId(@Param("requirementId") UUID requirementId);
}
