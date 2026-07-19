package com.company.scopery.platform.security;

import com.company.scopery.common.constant.ApiPaths;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;

class CookieUtilTest {

    @Test
    void refreshCookie_pathIsApiIamAuth() {
        ResponseCookie cookie = CookieUtil.buildRefreshCookie("token", 60_000L, false);
        assertThat(cookie.getPath()).isEqualTo(ApiPaths.IAM_AUTH);
    }

    @Test
    void refreshCookie_isHttpOnly() {
        ResponseCookie cookie = CookieUtil.buildRefreshCookie("token", 60_000L, false);
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    @Test
    void refreshCookie_usesConfiguredSecureFlag() {
        assertThat(CookieUtil.buildRefreshCookie("token", 60_000L, true).isSecure()).isTrue();
        assertThat(CookieUtil.buildRefreshCookie("token", 60_000L, false).isSecure()).isFalse();
    }

    @Test
    void clearRefreshCookie_usesSamePath() {
        ResponseCookie cookie = CookieUtil.clearRefreshCookie(false);
        assertThat(cookie.getPath()).isEqualTo(ApiPaths.IAM_AUTH);
        assertThat(cookie.getMaxAge().getSeconds()).isZero();
    }

    @Test
    void accessCookie_pathIsRoot() {
        ResponseCookie cookie = CookieUtil.buildAccessCookie("token", 60_000L, false);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    @Test
    void clearAccessCookie_usesRootPath() {
        ResponseCookie cookie = CookieUtil.clearAccessCookie(false);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge().getSeconds()).isZero();
    }
}
