package com.company.scopery.platform.security;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE  = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private static final String REFRESH_COOKIE_PATH = "/api/v1/iam/auth";

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
                .path(REFRESH_COOKIE_PATH)
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
                .path(REFRESH_COOKIE_PATH)
                .maxAge(0)
                .build();
    }
}
