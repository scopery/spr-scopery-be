package com.company.scopery.platform.security;

import com.company.scopery.common.constant.ApiPaths;
import org.springframework.http.ResponseCookie;

public final class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE  = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private CookieUtil() {}

    public static ResponseCookie buildAccessCookie(String token, long expirationMs, boolean secure) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(expirationMs / 1000)
                .build();
    }

    public static ResponseCookie buildRefreshCookie(String token, long expirationMs, boolean secure) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path(ApiPaths.IAM_AUTH)
                .maxAge(expirationMs / 1000)
                .build();
    }

    public static ResponseCookie clearAccessCookie(boolean secure) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
    }

    public static ResponseCookie clearRefreshCookie(boolean secure) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path(ApiPaths.IAM_AUTH)
                .maxAge(0)
                .build();
    }
}
