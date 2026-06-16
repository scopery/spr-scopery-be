package com.company.scopery.modules.iam.user.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.LoginCommand;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.application.response.LoginResponse;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.platform.security.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class IamAuthenticationService {

    private final IamUserRepository    userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final RefreshTokenService  refreshTokenService;

    public IamAuthenticationService(IamUserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    JwtService jwtService,
                                    RefreshTokenService refreshTokenService) {
        this.userRepository     = userRepository;
        this.passwordEncoder    = passwordEncoder;
        this.jwtService         = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional(readOnly = true)
    public AuthResult login(LoginCommand command) {
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

        return new AuthResult(user, accessToken, refreshToken);
    }

    public AuthResult refresh(String refreshToken) {
        UUID userId = refreshTokenService.validate(refreshToken)
                .orElseThrow(() -> new AppException(
                        IamErrorCatalog.INVALID_CREDENTIALS,
                        "Invalid or expired refresh token",
                        Map.of()));

        IamUser user = userRepository.findById(userId)
                .orElseThrow(() -> IamExceptions.iamUserNotFound(userId));

        // Rotate: revoke old, issue new
        refreshTokenService.revoke(refreshToken);
        String newRefreshToken = refreshTokenService.create(userId);
        String newAccessToken  = jwtService.generateToken(userId, user.username().value());

        return new AuthResult(user, newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public static LoginResponse toLoginResponse(IamUser user) {
        return new LoginResponse(
                user.id(),
                user.username().value(),
                user.email().value(),
                user.fullName());
    }
}
