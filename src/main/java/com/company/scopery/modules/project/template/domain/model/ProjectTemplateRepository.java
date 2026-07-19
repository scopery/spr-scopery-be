package com.company.scopery.modules.project.template.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateCategory;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;

import java.util.Optional;
import java.util.UUID;

public interface ProjectTemplateRepository {

    ProjectTemplate save(ProjectTemplate template);

    Optional<ProjectTemplate> findById(UUID id);

    boolean existsByCodeAndScope(String code, ProjectTemplateScope scope);

    boolean existsByCodeAndScopeAndWorkspaceId(String code, ProjectTemplateScope scope, UUID workspaceId);

    boolean existsByCodeAndScopeAndOrganizationId(String code, ProjectTemplateScope scope, UUID organizationId);

    PageResult<ProjectTemplate> search(
            ProjectTemplateScope scope,
            UUID workspaceId,
            UUID organizationId,
            ProjectTemplateStatus status,
            ProjectTemplateCategory category,
            String keyword,
            PageQuery pageQuery);
}
