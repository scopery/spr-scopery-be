package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.LoginCommand;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.platform.security.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoginAction {

    private final IamUserRepository    userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final RefreshTokenService  refreshTokenService;
    private final IamActivityLogger    activityLogger;

    public LoginAction(IamUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       IamActivityLogger activityLogger) {
        this.userRepository     = userRepository;
        this.passwordEncoder    = passwordEncoder;
        this.jwtService         = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.activityLogger     = activityLogger;
    }

    @Transactional
    public AuthResult execute(LoginCommand command) {
        Username usernameObj;
        try {
            usernameObj = Username.of(command.username());
        } catch (IllegalArgumentException e) {
            throw IamExceptions.invalidCredentials();
        }

        IamUser user = userRepository.findByUsername(usernameObj)
                .orElseThrow(IamExceptions::invalidCredentials);

        if (user.passwordHash() == null
                || !passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw IamExceptions.invalidCredentials();
        }

        if (user.status() != IamUserStatus.ACTIVE) {
            throw IamExceptions.userInactiveCannotLogin(user.username().value());
        }

        String accessToken  = jwtService.generateToken(user.id(), user.username().value());
        String refreshToken = refreshTokenService.create(user.id());

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, user.id(),
                IamActivityActions.LOGIN_IAM_USER, "User logged in: " + user.username().value());

        return new AuthResult(user, accessToken, refreshToken);
    }
}
