package com.company.scopery.modules.project.phasedefinition.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;

import java.util.Optional;
import java.util.UUID;

public interface PhaseDefinitionRepository {

    PhaseDefinition save(PhaseDefinition pd);

    Optional<PhaseDefinition> findById(UUID id);

    boolean existsByCodeAndScope(String code, PhaseDefinitionScope scope);

    boolean existsByCodeAndScopeAndWorkspaceId(String code, PhaseDefinitionScope scope, UUID workspaceId);

    boolean existsByCodeAndScopeAndOrganizationId(String code, PhaseDefinitionScope scope, UUID organizationId);

    boolean isUsedByAnyProject(UUID phaseDefinitionId);

    boolean isUsedByAnyTemplatePhase(UUID phaseDefinitionId);

    PageResult<PhaseDefinition> search(
            PhaseDefinitionScope scope,
            UUID organizationId,
            UUID workspaceId,
            String keyword,
            PhaseDefinitionStatus status,
            PageQuery pageQuery);
}
