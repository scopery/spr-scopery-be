package com.company.scopery.platform.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CORS contract for cookie-auth browsers (Phase 01 §15).
 * Origins must be explicit — never "*" with allowCredentials=true.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "scopery.cors")
public class CorsProperties {

    /**
     * Explicit allowed frontend origins. Never use "*".
     */
    private List<String> allowedOrigins = new ArrayList<>(List.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1:5173"
    ));

    private boolean allowCredentials = true;

    private List<String> allowedMethods = new ArrayList<>(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));

    private List<String> allowedHeaders = new ArrayList<>(List.of(
            "Authorization",
            "Content-Type",
            "X-XSRF-TOKEN",
            "X-Trace-Id",
            "Idempotency-Key",
            "X-Workspace-Id",
            "X-Actor-Id",
            "Last-Event-ID"
    ));

    private List<String> exposedHeaders = new ArrayList<>(List.of(
            "X-Trace-Id"
    ));

    private long maxAgeSeconds = 3600L;

    public void validateForCredentialedRequests() {
        if (!allowCredentials) {
            return;
        }
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            throw new IllegalStateException(
                    "scopery.cors.allowed-origins must be configured when allow-credentials=true");
        }
        for (String origin : allowedOrigins) {
            if (origin == null || origin.isBlank()) {
                throw new IllegalStateException("scopery.cors.allowed-origins must not contain blank values");
            }
            if ("*".equals(origin.trim())) {
                throw new IllegalStateException(
                        "scopery.cors.allowed-origins must not contain '*' when allow-credentials=true");
            }
        }
    }
}
