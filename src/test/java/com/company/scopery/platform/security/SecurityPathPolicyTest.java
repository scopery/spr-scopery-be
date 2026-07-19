package com.company.scopery.platform.security;

import com.company.scopery.common.constant.ApiPaths;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityPathPolicyTest {

    @Test
    void publicPaths_doNotContainApiWildcard() {
        assertThat(SecurityPathPolicy.infraPublicPaths()).noneMatch(p -> p.equals("/api/**"));
        assertThat(SecurityPathPolicy.publicAuthPostPaths()).noneMatch(p -> p.equals("/api/**"));
        assertThat(SecurityPathPolicy.healthPath()).isNotEqualTo("/api/**");
    }

    @Test
    void publicPaths_includeHealth() {
        assertThat(SecurityPathPolicy.healthPath()).isEqualTo(ApiPaths.HEALTH);
    }

    @Test
    void publicPaths_includeIamAuthBootstrapEndpoints() {
        assertThat(SecurityPathPolicy.publicAuthPostPaths()).containsExactly(
                SecurityPathPolicy.IAM_AUTH_LOGIN,
                SecurityPathPolicy.IAM_AUTH_REFRESH,
                SecurityPathPolicy.IAM_AUTH_LOGOUT,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_REQUEST,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_CONFIRM,
                SecurityPathPolicy.PORTAL_AUTH_LOGIN,
                SecurityPathPolicy.PORTAL_AUTH_ACCEPT_INVITE,
                SecurityPathPolicy.PORTAL_AUTH_LOGOUT);
    }

    @Test
    void csrfIgnoredPaths_includeIamAuthBootstrapEndpoints() {
        assertThat(SecurityPathPolicy.csrfIgnoredPaths()).contains(
                SecurityPathPolicy.IAM_AUTH_LOGIN,
                SecurityPathPolicy.IAM_AUTH_REFRESH,
                SecurityPathPolicy.IAM_AUTH_LOGOUT,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_REQUEST,
                ApiPaths.IAM_AUTH_PASSWORD_RESET_CONFIRM,
                ApiPaths.IAM_USERS,
                SecurityPathPolicy.PORTAL_AUTH_LOGIN,
                SecurityPathPolicy.PORTAL_AUTH_ACCEPT_INVITE,
                SecurityPathPolicy.PORTAL_AUTH_LOGOUT);
    }

    @Test
    void csrfIgnoredPaths_doNotContainPasswordChangeOrRevokeAll() {
        assertThat(SecurityPathPolicy.csrfIgnoredPaths())
                .doesNotContain(SecurityPathPolicy.IAM_AUTH_PASSWORD_CHANGE)
                .doesNotContain(SecurityPathPolicy.IAM_AUTH_SESSIONS_REVOKE_ALL);
    }

    @Test
    void csrfIgnoredPaths_doNotContainOldV1Auth() {
        // Old format was /api/v1/auth/... (without /iam/ namespace)
        assertThat(SecurityPathPolicy.csrfIgnoredPaths())
                .noneMatch(p -> p.contains("/v1/auth/") || p.contains("/v2/auth/"));
        assertThat(SecurityPathPolicy.publicAuthPostPaths())
                .noneMatch(p -> p.contains("/v1/auth/") || p.contains("/v2/auth/"));
    }

    @Test
    void passwordChangeAndRevokeAll_areRecognizedAsProtectedAuthPaths() {
        assertThat(SecurityPathPolicy.isPasswordChangePath(SecurityPathPolicy.IAM_AUTH_PASSWORD_CHANGE)).isTrue();
        assertThat(SecurityPathPolicy.isSessionsRevokeAllPath(SecurityPathPolicy.IAM_AUTH_SESSIONS_REVOKE_ALL)).isTrue();
        assertThat(SecurityPathPolicy.publicAuthPostPaths())
                .doesNotContain(SecurityPathPolicy.IAM_AUTH_PASSWORD_CHANGE)
                .doesNotContain(SecurityPathPolicy.IAM_AUTH_SESSIONS_REVOKE_ALL);
    }
}
