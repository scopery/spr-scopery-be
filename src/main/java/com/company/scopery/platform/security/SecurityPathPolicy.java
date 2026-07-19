package com.company.scopery.platform.security;

import com.company.scopery.common.constant.ApiPaths;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

/**
 * Explicit public and CSRF-exempt path contract for Phase 01 security baseline.
 * Protected endpoints under /api/iam/auth (password/change, sessions/revoke-all) are intentionally excluded.
 */
public final class SecurityPathPolicy {

    public static final String IAM_AUTH_LOGIN = ApiPaths.IAM_AUTH + "/login";
    public static final String IAM_AUTH_REFRESH = ApiPaths.IAM_AUTH + "/refresh";
    public static final String IAM_AUTH_LOGOUT = ApiPaths.IAM_AUTH + "/logout";
    public static final String IAM_AUTH_PASSWORD_CHANGE = ApiPaths.IAM_AUTH + "/password/change";
    public static final String IAM_AUTH_SESSIONS_REVOKE_ALL = ApiPaths.IAM_AUTH + "/sessions/revoke-all";

    public static final String PORTAL_AUTH_LOGIN = ClientPortalApiPaths.PORTAL_AUTH + "/login";
    public static final String PORTAL_AUTH_ACCEPT_INVITE = ClientPortalApiPaths.PORTAL_AUTH + "/accept-invite";
    public static final String PORTAL_AUTH_REFRESH = ClientPortalApiPaths.PORTAL_AUTH + "/refresh";
    public static final String PORTAL_AUTH_LOGOUT = ClientPortalApiPaths.PORTAL_AUTH + "/logout";

    private static final List<String> INFRA_PUBLIC_PATHS = List.of(
            "/actuator/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    );

    private SecurityPathPolicy() {}

    /** Default infra public paths (Swagger + actuator health). */
    public static List<String> defaultInfraPublicPaths() {
        return INFRA_PUBLIC_PATHS;
    }

    /** Ant-style paths that are always public (method-agnostic matchers). */
    public static List<String> infraPublicPaths() {
        return INFRA_PUBLIC_PATHS;
    }

    public static String healthPath() {
        return ApiPaths.HEALTH;
    }

    /** Explicit CSRF-exempt request matchers (auth bootstrap + registration). */
    public static RequestMatcher[] csrfIgnoredMatchers() {
        return new RequestMatcher[] {
                new AntPathRequestMatcher(IAM_AUTH_LOGIN, HttpMethod.POST.name()),
                new AntPathRequestMatcher(IAM_AUTH_REFRESH, HttpMethod.POST.name()),
                new AntPathRequestMatcher(IAM_AUTH_LOGOUT, HttpMethod.POST.name()),
                new AntPathRequestMatcher(ApiPaths.IAM_AUTH_PASSWORD_RESET_REQUEST, HttpMethod.POST.name()),
                new AntPathRequestMatcher(ApiPaths.IAM_AUTH_PASSWORD_RESET_CONFIRM, HttpMethod.POST.name()),
                new AntPathRequestMatcher(ApiPaths.IAM_USERS, HttpMethod.POST.name()),
                new AntPathRequestMatcher(PORTAL_AUTH_LOGIN, HttpMethod.POST.name()),
                new AntPathRequestMatcher(PORTAL_AUTH_ACCEPT_INVITE, HttpMethod.POST.name()),
                new AntPathRequestMatcher(PORTAL_AUTH_LOGOUT, HttpMethod.POST.name())
        };
    }

    /** Path strings used by CSRF-exempt matchers (for unit assertions). */
    public static List<String> csrfIgnoredPaths() {
        return List.of(
                IAM_AUTH_LOGIN,
                IAM_AUTH_REFRESH,
                IAM_AUTH_LOGOUT,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_REQUEST,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_CONFIRM,
                ApiPaths.IAM_USERS,
                PORTAL_AUTH_LOGIN,
                PORTAL_AUTH_ACCEPT_INVITE,
                PORTAL_AUTH_LOGOUT
        );
    }

    /** Explicit public auth POST paths (excluding password/change and sessions/revoke-all). */
    public static List<String> publicAuthPostPaths() {
        return List.of(
                IAM_AUTH_LOGIN,
                IAM_AUTH_REFRESH,
                IAM_AUTH_LOGOUT,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_REQUEST,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_CONFIRM,
                PORTAL_AUTH_LOGIN,
                PORTAL_AUTH_ACCEPT_INVITE,
                PORTAL_AUTH_LOGOUT
        );
    }

    public static boolean isPasswordChangePath(String path) {
        return IAM_AUTH_PASSWORD_CHANGE.equals(path);
    }

    public static boolean isSessionsRevokeAllPath(String path) {
        return IAM_AUTH_SESSIONS_REVOKE_ALL.equals(path);
    }

    public static boolean containsApiVersionPrefix(String path) {
        return path != null && (path.contains("/api/v1") || path.contains("/api/v2"));
    }
}
