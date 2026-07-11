package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.RefreshTokenCommand;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.platform.security.RefreshTokenService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class RefreshTokenAction {

    private final IamUserRepository   userRepository;
    private final JwtService          jwtService;
    private final RefreshTokenService refreshTokenService;
    private final IamActivityLogger   activityLogger;

    public RefreshTokenAction(IamUserRepository userRepository,
                              JwtService jwtService,
                              RefreshTokenService refreshTokenService,
                              IamActivityLogger activityLogger) {
        this.userRepository     = userRepository;
        this.jwtService         = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.activityLogger     = activityLogger;
    }

    @Transactional
    public AuthResult execute(RefreshTokenCommand command) {
        UUID userId = refreshTokenService.validate(command.refreshToken())
                .orElseThrow(() -> new AppException(
                        IamErrorCatalog.INVALID_CREDENTIALS,
                        "Invalid or expired refresh token",
                        Map.of()));

        IamUser user = userRepository.findById(userId)
                .orElseThrow(() -> IamExceptions.iamUserNotFound(userId));

        refreshTokenService.revoke(command.refreshToken());
        String newRefreshToken = refreshTokenService.create(userId);
        String newAccessToken  = jwtService.generateToken(userId, user.username().value());

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, userId,
                IamActivityActions.REFRESH_IAM_USER_TOKEN, "Token refreshed for user: " + user.username().value());

        return new AuthResult(user, newAccessToken, newRefreshToken);
    }
}
