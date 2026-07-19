package com.company.scopery.modules.project.template.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataProjectTemplateJpaRepository
        extends JpaRepository<ProjectTemplateJpaEntity, UUID>,
                JpaSpecificationExecutor<ProjectTemplateJpaEntity> {

    boolean existsByCodeAndScope(String code, String scope);

    boolean existsByCodeAndScopeAndWorkspaceId(String code, String scope, UUID workspaceId);

    boolean existsByCodeAndScopeAndOrganizationId(String code, String scope, UUID organizationId);
}
