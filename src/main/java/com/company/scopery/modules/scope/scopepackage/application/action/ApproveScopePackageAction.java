package com.company.scopery.modules.scope.scopepackage.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.scopepackage.application.command.ApproveScopePackageCommand;
import com.company.scopery.modules.scope.scopepackage.application.response.ScopePackageResponse;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApproveScopePackageAction {
    private final ScopePackageRepository packages;
    private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ScopeActivityLogger activityLogger;

    public ApproveScopePackageAction(ScopePackageRepository packages, ScopeAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser, ScopeActivityLogger activityLogger) {
        this.packages = packages;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ScopePackageResponse execute(ApproveScopePackageCommand command) {
        authorization.requireScopeApprove(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        var pkg = packages.findByIdAndProjectId(command.packageId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.packageNotFound(command.packageId()));
        if (!pkg.isEditable() && pkg.status().name().equals("APPROVED")) {
            throw ScopeExceptions.packageImmutable(command.packageId());
        }
        packages.clearCurrentFlag(command.projectId());
        pkg = packages.save(pkg.approve(actor.id()));
        activityLogger.logSuccess(ScopeEntityTypes.PACKAGE, pkg.id(), ScopeActivityActions.PACKAGE_APPROVED, "Scope package approved");
        return ScopePackageResponse.from(pkg);
    }
}
