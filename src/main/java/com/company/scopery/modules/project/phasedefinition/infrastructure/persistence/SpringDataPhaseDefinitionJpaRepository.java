package com.company.scopery.modules.project.phasedefinition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpringDataPhaseDefinitionJpaRepository
        extends JpaRepository<PhaseDefinitionJpaEntity, UUID>,
                JpaSpecificationExecutor<PhaseDefinitionJpaEntity> {

    boolean existsByCodeAndScope(String code, String scope);

    boolean existsByCodeAndScopeAndWorkspaceId(String code, String scope, UUID workspaceId);

    boolean existsByCodeAndScopeAndOrganizationId(String code, String scope, UUID organizationId);

    @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN true ELSE false END " +
           "FROM com.company.scopery.modules.project.projectphase.infrastructure.persistence.ProjectPhaseJpaEntity pp " +
           "WHERE pp.phaseDefinitionId = :id")
    boolean isUsedByAnyProject(@Param("id") UUID id);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
                   "FROM project_template_phase WHERE phase_definition_id = :id",
            nativeQuery = true)
    boolean isUsedByAnyTemplatePhase(@Param("id") UUID id);
}
