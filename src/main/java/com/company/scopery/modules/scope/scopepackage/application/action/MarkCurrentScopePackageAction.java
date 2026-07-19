package com.company.scopery.modules.scope.scopepackage.application.action;
import com.company.scopery.modules.scope.scopepackage.application.command.MarkCurrentScopePackageCommand;
import com.company.scopery.modules.scope.scopepackage.application.response.ScopePackageResponse;
import com.company.scopery.modules.scope.scopepackage.domain.enums.ScopePackageStatus;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class MarkCurrentScopePackageAction {
    private final ScopePackageRepository packages;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public MarkCurrentScopePackageAction(ScopePackageRepository packages, ScopeAuthorizationService authorization,
                                         ScopeActivityLogger activityLogger) {
        this.packages = packages; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public ScopePackageResponse execute(MarkCurrentScopePackageCommand command) {
        authorization.requireScopeApprove(command.projectId());
        var pkg = packages.findByIdAndProjectId(command.packageId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.packageNotFound(command.packageId()));
        if (pkg.status() != ScopePackageStatus.APPROVED && pkg.status() != ScopePackageStatus.CURRENT) {
            throw ScopeExceptions.packageNotApproved(command.packageId());
        }
        packages.clearCurrentFlag(command.projectId());
        try {
            pkg = packages.save(pkg.markCurrent());
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.packageNotApproved(command.packageId());
        }
        activityLogger.logSuccess(ScopeEntityTypes.PACKAGE, pkg.id(), ScopeActivityActions.PACKAGE_MARKED_CURRENT,
                "Scope package marked current");
        return ScopePackageResponse.from(pkg);
    }
}
