package com.company.scopery.platform.web.idempotency;

import com.company.scopery.common.constant.ApiPaths;
import com.company.scopery.common.exception.PlatformErrorCatalog;
import com.company.scopery.common.privacy.SensitiveDataRedactor;
import com.company.scopery.platform.web.RequestContext;
import com.company.scopery.platform.web.RequestContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import org.springframework.util.AntPathMatcher;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class IdempotencyKeyFilter extends OncePerRequestFilter {

    public static final String HEADER = "Idempotency-Key";

    private final IdempotencyKeyJpaRepository repository;
    private final SensitiveDataRedactor redactor;
    private final ObjectMapper objectMapper;
    private final int ttlHours;
    private final int inProgressLockSeconds;
    private final boolean requireEnabled;
    private final List<String> requireOnPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public IdempotencyKeyFilter(IdempotencyKeyJpaRepository repository,
                                SensitiveDataRedactor redactor,
                                ObjectMapper objectMapper,
                                @Value("${scopery.platform.idempotency.ttl-hours:24}") int ttlHours,
                                @Value("${scopery.platform.idempotency.in-progress-lock-seconds:120}") int inProgressLockSeconds,
                                @Value("${scopery.platform.idempotency.require-enabled:false}") boolean requireEnabled,
                                @Value("${scopery.platform.idempotency.require-on-paths:/api/projects/*/tasks,/api/projects/*/scope-packages,/api/projects/*/quotes}") String requireOnPaths) {
        this.repository = repository;
        this.redactor = redactor;
        this.objectMapper = objectMapper;
        this.ttlHours = ttlHours;
        this.inProgressLockSeconds = inProgressLockSeconds;
        this.requireEnabled = requireEnabled;
        this.requireOnPaths = List.of(requireOnPaths.split(",")).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())
                || !request.getRequestURI().startsWith(ApiPaths.BASE_PATH + "/")) {
            return true;
        }
        if (requireEnabled && requiresIdempotencyKey(request.getRequestURI())
                && (request.getHeader(HEADER) == null || request.getHeader(HEADER).isBlank())) {
            return false;
        }
        return request.getHeader(HEADER) == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String rawKey = request.getHeader(HEADER);
        if (rawKey == null || rawKey.isBlank()) {
            if (requireEnabled && requiresIdempotencyKey(request.getRequestURI())) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR",
                        "Idempotency-Key header is required for this endpoint");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }
        if (rawKey.length() > 255) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Invalid Idempotency-Key");
            return;
        }

        CachedBodyRequest cachedRequest = new CachedBodyRequest(request);
        String keyHash = hash(scope(cachedRequest) + ":" + rawKey);
        String requestHash = hash(cachedRequest.getCachedBody());
        Instant now = Instant.now();

        var existing = repository.findByKeyHashAndExpiresAtAfter(keyHash, now);
        if (existing.isPresent()) {
            IdempotencyKeyJpaEntity value = existing.get();
            if (!requestHash.equals(value.getRequestHash())) {
                writeError(response, HttpServletResponse.SC_CONFLICT,
                        PlatformErrorCatalog.PLATFORM_IDEMPOTENCY_CONFLICT.code(),
                        PlatformErrorCatalog.PLATFORM_IDEMPOTENCY_CONFLICT.defaultMessage());
                return;
            }
            if (IdempotencyKeyJpaEntity.STATUS_COMPLETED.equals(value.getStatus())
                    && value.getResponseStatus() != null
                    && value.getResponseBody() != null) {
                replay(response, value);
                return;
            }
            if (IdempotencyKeyJpaEntity.STATUS_IN_PROGRESS.equals(value.getStatus())
                    && value.getLockedUntil() != null
                    && value.getLockedUntil().isAfter(now)) {
                writeError(response, HttpServletResponse.SC_CONFLICT,
                        PlatformErrorCatalog.PLATFORM_IDEMPOTENCY_IN_PROGRESS.code(),
                        PlatformErrorCatalog.PLATFORM_IDEMPOTENCY_IN_PROGRESS.defaultMessage());
                return;
            }
        }

        if (existing.isEmpty()) {
            IdempotencyKeyJpaEntity inProgress = new IdempotencyKeyJpaEntity();
            inProgress.setKeyHash(keyHash);
            inProgress.setRequestMethod(cachedRequest.getMethod());
            inProgress.setRequestPath(cachedRequest.getRequestURI());
            inProgress.setRequestHash(requestHash);
            inProgress.setStatus(IdempotencyKeyJpaEntity.STATUS_IN_PROGRESS);
            inProgress.setLockedUntil(now.plus(inProgressLockSeconds, ChronoUnit.SECONDS));
            inProgress.setCreatedAt(now);
            inProgress.setUpdatedAt(now);
            inProgress.setExpiresAt(now.plus(ttlHours, ChronoUnit.HOURS));
            try {
                repository.saveAndFlush(inProgress);
            } catch (DataIntegrityViolationException conflict) {
                writeError(response, HttpServletResponse.SC_CONFLICT,
                        PlatformErrorCatalog.PLATFORM_IDEMPOTENCY_IN_PROGRESS.code(),
                        PlatformErrorCatalog.PLATFORM_IDEMPOTENCY_IN_PROGRESS.defaultMessage());
                return;
            }
        }

        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(cachedRequest, wrapper);
        byte[] body = wrapper.getContentAsByteArray();
        Instant completedAt = Instant.now();

        IdempotencyKeyJpaEntity entity = repository.findById(keyHash).orElseGet(IdempotencyKeyJpaEntity::new);
        entity.setKeyHash(keyHash);
        entity.setRequestMethod(cachedRequest.getMethod());
        entity.setRequestPath(cachedRequest.getRequestURI());
        entity.setRequestHash(requestHash);
        entity.setUpdatedAt(completedAt);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(completedAt);
        }
        if (entity.getExpiresAt() == null) {
            entity.setExpiresAt(completedAt.plus(ttlHours, ChronoUnit.HOURS));
        }
        entity.setLockedUntil(null);

        if (wrapper.getStatus() >= 500) {
            entity.setStatus(IdempotencyKeyJpaEntity.STATUS_FAILED);
            entity.setResponseStatus(wrapper.getStatus());
            entity.setResponseBody(null);
            entity.setContentType(null);
        } else if (body.length > 0 && isJson(wrapper.getContentType())) {
            entity.setStatus(IdempotencyKeyJpaEntity.STATUS_COMPLETED);
            entity.setResponseStatus(wrapper.getStatus());
            entity.setResponseBody(redactor.redactJson(new String(body, StandardCharsets.UTF_8)));
            entity.setContentType(wrapper.getContentType());
        } else {
            entity.setStatus(IdempotencyKeyJpaEntity.STATUS_COMPLETED);
            entity.setResponseStatus(wrapper.getStatus());
            entity.setResponseBody(body.length == 0 ? "{}" : redactor.redactText(new String(body, StandardCharsets.UTF_8)));
            entity.setContentType(wrapper.getContentType());
        }

        try {
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ignored) {
            // Concurrent completion — replay will serve the winner on retry.
        }
        wrapper.copyBodyToResponse();
    }

    private boolean requiresIdempotencyKey(String requestUri) {
        for (String pattern : requireOnPaths) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    private void replay(HttpServletResponse response, IdempotencyKeyJpaEntity value) throws IOException {
        response.setStatus(value.getResponseStatus());
        if (value.getContentType() != null) {
            response.setContentType(value.getContentType());
        }
        response.setHeader("Idempotency-Replayed", "true");
        response.getOutputStream().write(value.getResponseBody().getBytes(StandardCharsets.UTF_8));
    }

    private void writeError(HttpServletResponse response, int status, String errorCode, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String traceId = RequestContextHolder.getContext()
                .map(RequestContext::getTraceId)
                .orElse(null);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("status", status);
        body.put("error", errorCode);
        body.put("errorCode", errorCode);
        body.put("message", message);
        body.put("details", List.of());
        body.put("traceId", traceId);
        body.put("timestamp", Instant.now().toString());
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private String scope(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication == null ? "anonymous" : authentication.getName();
        return actor + ":" + request.getMethod() + ":" + request.getRequestURI();
    }

    private boolean isJson(String contentType) {
        return contentType != null && contentType.contains("json");
    }

    private String hash(String value) {
        return hash(value.getBytes(StandardCharsets.UTF_8));
    }

    private String hash(byte[] value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    static final class CachedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        CachedBodyRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.cachedBody = request.getInputStream().readAllBytes();
        }

        byte[] getCachedBody() {
            return cachedBody;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // no-op
                }

                @Override
                public int read() {
                    return inputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
