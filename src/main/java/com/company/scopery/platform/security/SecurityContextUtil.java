package com.company.scopery.platform.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityContextUtil {

    private SecurityContextUtil() {}

    /**
     * Returns the authenticated user's UUID from the JWT token stored in the security context.
     * Falls back to the provided headerValue when available; uses JWT only as a secondary source.
     */
    public static UUID resolveActorId(UUID headerValue) {
        if (headerValue != null) return headerValue;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtAuthFilter.MapTokenDetails details) {
            return details.subjectId();
        }
        return null;
    }
}
