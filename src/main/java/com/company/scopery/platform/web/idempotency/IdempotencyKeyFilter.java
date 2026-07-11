package com.company.scopery.platform.web.idempotency;

import com.company.scopery.common.constant.ApiPaths;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Component
public class IdempotencyKeyFilter extends OncePerRequestFilter {
    public static final String HEADER = "Idempotency-Key";
    private final IdempotencyKeyJpaRepository repository;
    public IdempotencyKeyFilter(IdempotencyKeyJpaRepository repository) { this.repository = repository; }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !request.getRequestURI().startsWith(ApiPaths.BASE_PATH + "/")
                || request.getHeader(HEADER) == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String rawKey = request.getHeader(HEADER);
        if (rawKey.isBlank() || rawKey.length() > 255) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Idempotency-Key");
            return;
        }
        String keyHash = hash(scope(request) + ":" + rawKey);
        var cached = repository.findByKeyHashAndExpiresAtAfter(keyHash, Instant.now());
        if (cached.isPresent()) {
            var value = cached.get();
            response.setStatus(value.getResponseStatus());
            if (value.getContentType() != null) response.setContentType(value.getContentType());
            response.setHeader("Idempotency-Replayed", "true");
            response.getOutputStream().write(value.getResponseBody().getBytes(StandardCharsets.UTF_8));
            return;
        }

        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrapper);
        byte[] body = wrapper.getContentAsByteArray();
        if (wrapper.getStatus() < 500 && body.length > 0 && isJson(wrapper.getContentType())) {
            IdempotencyKeyJpaEntity entity = new IdempotencyKeyJpaEntity();
            entity.setKeyHash(keyHash); entity.setRequestMethod(request.getMethod());
            entity.setRequestPath(request.getRequestURI()); entity.setResponseStatus(wrapper.getStatus());
            entity.setResponseBody(new String(body, StandardCharsets.UTF_8));
            entity.setContentType(wrapper.getContentType()); entity.setCreatedAt(Instant.now());
            entity.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
            try { repository.saveAndFlush(entity); } catch (DataIntegrityViolationException ignored) { }
        }
        wrapper.copyBodyToResponse();
    }

    private String scope(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication == null ? "anonymous" : authentication.getName();
        return actor + ":" + request.getMethod() + ":" + request.getRequestURI();
    }

    private boolean isJson(String contentType) { return contentType != null && contentType.contains("json"); }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
