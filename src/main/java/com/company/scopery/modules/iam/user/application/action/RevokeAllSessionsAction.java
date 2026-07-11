package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.user.application.command.RevokeAllSessionsCommand;
import com.company.scopery.platform.security.RefreshTokenService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RevokeAllSessionsAction {

    private final CurrentUserAuthorizationService currentUserService;
    private final RefreshTokenService refreshTokenService;
    private final IamActivityLogger activityLogger;

    public RevokeAllSessionsAction(CurrentUserAuthorizationService currentUserService,
                                   RefreshTokenService refreshTokenService,
                                   IamActivityLogger activityLogger) {
        this.currentUserService = currentUserService;
        this.refreshTokenService = refreshTokenService;
        this.activityLogger = activityLogger;
    }

    @Transactional(readOnly = true)
    public void execute(RevokeAllSessionsCommand command) {
        var user = currentUserService.resolveCurrentUser();
        refreshTokenService.revokeAll(user.id());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, user.id(),
                IamActivityActions.REVOKE_IAM_USER_SESSIONS, "All sessions revoked for user: " + user.id());
    }
}
