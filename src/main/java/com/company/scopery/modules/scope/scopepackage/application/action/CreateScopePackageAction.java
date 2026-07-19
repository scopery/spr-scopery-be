package com.company.scopery.modules.scope.scopepackage.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.scope.scopepackage.application.command.CreateScopePackageCommand;
import com.company.scopery.modules.scope.scopepackage.application.response.ScopePackageResponse;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackage;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateScopePackageAction {
    private final ProjectRepository projects;
    private final ScopePackageRepository packages;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;

    public CreateScopePackageAction(ProjectRepository projects, ScopePackageRepository packages,
                                    ScopeAuthorizationService authorization, ScopeActivityLogger activityLogger) {
        this.projects = projects;
        this.packages = packages;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ScopePackageResponse execute(CreateScopePackageCommand command) {
        authorization.requireScopeCreate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ScopeExceptions.projectArchived(command.projectId());
        }
        if (packages.existsByProjectIdAndCode(command.projectId(), command.code().trim())) {
            throw ScopeExceptions.codeExists(command.code());
        }
        ScopePackage pkg = packages.save(ScopePackage.create(
                project.id(), project.workspaceId(), command.code().trim(), command.name().trim(),
                command.description(), MDC.get("traceId")));
        activityLogger.logSuccess(ScopeEntityTypes.PACKAGE, pkg.id(), ScopeActivityActions.PACKAGE_CREATED, "Scope package created");
        return ScopePackageResponse.from(pkg);
    }
}
