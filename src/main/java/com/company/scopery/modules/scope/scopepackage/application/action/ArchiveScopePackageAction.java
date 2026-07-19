package com.company.scopery.modules.scope.scopepackage.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.scopepackage.application.command.ArchiveScopePackageCommand;
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
public class ArchiveScopePackageAction {
    private final ScopePackageRepository packages;
    private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ScopeActivityLogger activityLogger;
    public ArchiveScopePackageAction(ScopePackageRepository packages, ScopeAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser, ScopeActivityLogger activityLogger) {
        this.packages = packages; this.authorization = authorization;
        this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public ScopePackageResponse execute(ArchiveScopePackageCommand command) {
        authorization.requireScopeUpdate(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        var pkg = packages.findByIdAndProjectId(command.packageId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.packageNotFound(command.packageId()));
        pkg = packages.save(pkg.archive(actor.id()));
        activityLogger.logSuccess(ScopeEntityTypes.PACKAGE, pkg.id(), ScopeActivityActions.PACKAGE_ARCHIVED,
                "Scope package archived");
        return ScopePackageResponse.from(pkg);
    }
}
