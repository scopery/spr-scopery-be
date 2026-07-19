package com.company.scopery.modules.project.shared.support;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Resolves workspace vs system authorization for project templates.
 * SYSTEM/ORGANIZATION templates use SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION as platform admin gate
 * for write operations; VIEW for those scopes is also gated by the same system right in Phase 11.
 */
@Component
public class TemplateAccessSupport {

    private final ProjectWorkspaceAuthorizationService workspaceAuth;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public TemplateAccessSupport(ProjectWorkspaceAuthorizationService workspaceAuth,
                                 IamSystemAuthorizationService systemAuthorizationService) {
        this.workspaceAuth = workspaceAuth;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    public void requireView(ProjectTemplate template) {
        authorize(template, workspaceAuth::requireTemplateView);
    }

    public void requireCreate(ProjectTemplateScope scope, UUID workspaceId) {
        if (scope == ProjectTemplateScope.WORKSPACE) {
            if (workspaceId == null) {
                throw ProjectExceptions.projectTemplateInvalidScope("workspaceId is required for WORKSPACE scope");
            }
            workspaceAuth.requireTemplateCreate(workspaceId);
        } else {
            requireSystemManage();
        }
    }

    public void requireUpdate(ProjectTemplate template) {
        authorize(template, workspaceAuth::requireTemplateUpdate);
    }

    public void requirePublish(ProjectTemplate template) {
        authorize(template, workspaceAuth::requireTemplatePublish);
    }

    public void requireArchive(ProjectTemplate template) {
        authorize(template, workspaceAuth::requireTemplateArchive);
    }

    public void requireApply(UUID targetWorkspaceId) {
        workspaceAuth.requireTemplateApply(targetWorkspaceId);
        workspaceAuth.requireProjectCreate(targetWorkspaceId);
    }

    private void authorize(ProjectTemplate template, Consumer<UUID> workspaceCheck) {
        if (template.scope() == ProjectTemplateScope.WORKSPACE) {
            if (template.workspaceId() == null) {
                throw ProjectExceptions.projectTemplateAccessDenied(template.id());
            }
            workspaceCheck.accept(template.workspaceId());
        } else {
            requireSystemManage();
        }
    }

    private void requireSystemManage() {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION.legacyRightCode());
    }
}
