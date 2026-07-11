package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.user.application.command.LogoutCommand;
import com.company.scopery.platform.security.RefreshTokenService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class LogoutAction {

    private final RefreshTokenService refreshTokenService;
    private final IamActivityLogger activityLogger;

    public LogoutAction(RefreshTokenService refreshTokenService, IamActivityLogger activityLogger) {
        this.refreshTokenService = refreshTokenService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(LogoutCommand command) {
        UUID userId = refreshTokenService.validate(command.refreshToken()).orElse(null);
        refreshTokenService.revoke(command.refreshToken());
        if (userId != null) {
            activityLogger.logSuccess(IamEntityTypes.IAM_USER, userId,
                    IamActivityActions.LOGOUT_IAM_USER, "User logged out: " + userId);
        }
    }
}
