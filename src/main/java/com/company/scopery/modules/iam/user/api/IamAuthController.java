package com.company.scopery.modules.iam.user.api;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.user.api.request.LoginRequest;
import com.company.scopery.modules.iam.user.application.IamAuthenticationService;
import com.company.scopery.modules.iam.user.application.command.LoginCommand;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.application.response.LoginResponse;
import com.company.scopery.platform.security.CookieUtil;
import com.company.scopery.platform.security.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
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

    private final IamAuthenticationService authService;
    private final JwtProperties            jwtProperties;

    public IamAuthController(IamAuthenticationService authService, JwtProperties jwtProperties) {
        this.authService   = authService;
        this.jwtProperties = jwtProperties;
    }

    @Operation(summary = "Login — returns user info; access_token + refresh_token set as HTTP-only cookies")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(new LoginCommand(request.username(), request.password()));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken()).toString())
                .body(ApiResponse.success(IamAuthenticationService.toLoginResponse(result.user())));
    }

    @Operation(summary = "Refresh access token using the refresh_token cookie; rotates both tokens")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        AuthResult result = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken()).toString())
                .body(ApiResponse.success(IamAuthenticationService.toLoginResponse(result.user())));
    }

    @Operation(summary = "Logout — clears cookies and revokes the refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        authService.logout(refreshToken);
        boolean secure = jwtProperties.isCookieSecure();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.clearAccessCookie(secure).toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtil.clearRefreshCookie(secure).toString())
                .body(ApiResponse.success(null));
    }

    private org.springframework.http.ResponseCookie accessCookie(String token) {
        return CookieUtil.buildAccessCookie(token, jwtProperties.getExpirationMs(), jwtProperties.isCookieSecure());
    }

    private org.springframework.http.ResponseCookie refreshCookie(String token) {
        return CookieUtil.buildRefreshCookie(token, jwtProperties.getRefreshExpirationMs(), jwtProperties.isCookieSecure());
    }
}
