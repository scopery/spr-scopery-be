package com.company.scopery.modules.iam.user.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.user.application.action.ChangePasswordAction;
import com.company.scopery.modules.iam.user.application.action.ConfirmPasswordResetAction;
import com.company.scopery.modules.iam.user.application.action.LoginAction;
import com.company.scopery.modules.iam.user.application.action.LogoutAction;
import com.company.scopery.modules.iam.user.application.action.RefreshTokenAction;
import com.company.scopery.modules.iam.user.application.action.RequestPasswordResetAction;
import com.company.scopery.modules.iam.user.application.action.RevokeAllSessionsAction;
import com.company.scopery.modules.iam.user.application.command.ChangePasswordCommand;
import com.company.scopery.modules.iam.user.application.command.ConfirmPasswordResetCommand;
import com.company.scopery.modules.iam.user.application.command.LoginCommand;
import com.company.scopery.modules.iam.user.application.command.LogoutCommand;
import com.company.scopery.modules.iam.user.application.command.RefreshTokenCommand;
import com.company.scopery.modules.iam.user.application.command.RequestPasswordResetCommand;
import com.company.scopery.modules.iam.user.application.command.RevokeAllSessionsCommand;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.application.response.LoginResponse;
import com.company.scopery.modules.iam.user.http.request.ChangePasswordRequest;
import com.company.scopery.modules.iam.user.http.request.LoginRequest;
import com.company.scopery.modules.iam.user.http.request.PasswordResetConfirmRequest;
import com.company.scopery.modules.iam.user.http.request.PasswordResetRequest;
import com.company.scopery.platform.security.CookieUtil;
import com.company.scopery.platform.security.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "IAM - Auth")
@RestController
@RequestMapping(IamApiPaths.IAM_AUTH)
public class IamAuthController {

    private final LoginAction                loginAction;
    private final RefreshTokenAction         refreshTokenAction;
    private final LogoutAction               logoutAction;
    private final ChangePasswordAction         changePasswordAction;
    private final RequestPasswordResetAction   requestPasswordResetAction;
    private final ConfirmPasswordResetAction   confirmPasswordResetAction;
    private final RevokeAllSessionsAction      revokeAllSessionsAction;
    private final JwtProperties                jwtProperties;

    public IamAuthController(LoginAction loginAction,
                             RefreshTokenAction refreshTokenAction,
                             LogoutAction logoutAction,
                             ChangePasswordAction changePasswordAction,
                             RequestPasswordResetAction requestPasswordResetAction,
                             ConfirmPasswordResetAction confirmPasswordResetAction,
                             RevokeAllSessionsAction revokeAllSessionsAction,
                             JwtProperties jwtProperties) {
        this.loginAction = loginAction;
        this.refreshTokenAction = refreshTokenAction;
        this.logoutAction = logoutAction;
        this.changePasswordAction = changePasswordAction;
        this.requestPasswordResetAction = requestPasswordResetAction;
        this.confirmPasswordResetAction = confirmPasswordResetAction;
        this.revokeAllSessionsAction = revokeAllSessionsAction;
        this.jwtProperties = jwtProperties;
    }

    @Operation(summary = "Login — returns user info; access_token + refresh_token set as HTTP-only cookies")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginAction.execute(new LoginCommand(request.username(), request.password()));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken()).toString())
                .body(ApiResponse.success(LoginResponse.from(result.user())));
    }

    @Operation(summary = "Refresh access token using the refresh_token cookie; rotates both tokens")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        AuthResult result = refreshTokenAction.execute(new RefreshTokenCommand(refreshToken));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken()).toString())
                .body(ApiResponse.success(LoginResponse.from(result.user())));
    }

    @Operation(summary = "Logout — clears cookies and revokes the refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        logoutAction.execute(new LogoutCommand(refreshToken));
        boolean secure = jwtProperties.isCookieSecure();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.clearAccessCookie(secure).toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtil.clearRefreshCookie(secure).toString())
                .body(ApiResponse.success(null));
    }

    @PostMapping("/password/change")
    @Operation(summary = "Change password and revoke all refresh sessions")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        changePasswordAction.execute(new ChangePasswordCommand(
                request.currentPassword(), request.newPassword()));
        return ApiResponse.success(null);
    }

    @PostMapping("/password/reset-request")
    @Operation(summary = "Request password reset without disclosing account existence")
    public ResponseEntity<ApiResponse<Void>> resetRequest(@Valid @RequestBody PasswordResetRequest request) {
        requestPasswordResetAction.execute(new RequestPasswordResetCommand(request.email()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(null));
    }

    @PostMapping("/password/reset-confirm")
    @Operation(summary = "Confirm password reset and revoke all refresh sessions")
    public ApiResponse<Void> resetConfirm(@Valid @RequestBody PasswordResetConfirmRequest request) {
        confirmPasswordResetAction.execute(new ConfirmPasswordResetCommand(
                request.token(), request.newPassword()));
        return ApiResponse.success(null);
    }

    @PostMapping("/sessions/revoke-all")
    @Operation(summary = "Revoke all refresh sessions for the authenticated user")
    public ApiResponse<Void> revokeAllSessions() {
        revokeAllSessionsAction.execute(new RevokeAllSessionsCommand());
        return ApiResponse.success(null);
    }

    private org.springframework.http.ResponseCookie accessCookie(String token) {
        return CookieUtil.buildAccessCookie(token, jwtProperties.getExpirationMs(), jwtProperties.isCookieSecure());
    }

    private org.springframework.http.ResponseCookie refreshCookie(String token) {
        return CookieUtil.buildRefreshCookie(token, jwtProperties.getRefreshExpirationMs(), jwtProperties.isCookieSecure());
    }
}
