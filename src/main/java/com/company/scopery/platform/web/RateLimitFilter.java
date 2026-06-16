package com.company.scopery.platform.web;

import com.company.scopery.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private static final String RATE_LIMIT_KEY_PREFIX = "scopery:rate_limit:";
    private static final String RETRY_AFTER_HEADER    = "Retry-After";
    private static final String TRACE_ID_HEADER       = "X-Trace-Id";
    private static final String RATE_LIMIT_ERROR_CODE = "RATE_LIMIT_EXCEEDED";

    private static final String[] SKIP_PREFIXES = {
        "/actuator/",
        "/swagger-ui",
        "/v3/api-docs"
    };

    private final RedisTemplate<String, Object> redisTemplate;
    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(RedisTemplate<String, Object> redisTemplate,
                           RateLimitProperties properties,
                           ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.properties    = properties;
        this.objectMapper  = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!properties.isEnabled()) return true;
        String path = request.getRequestURI();
        for (String prefix : SKIP_PREFIXES) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String ip  = resolveClientIp(request);
        String key = RATE_LIMIT_KEY_PREFIX + ip;

        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (Long.valueOf(1L).equals(count)) {
                redisTemplate.expire(key, properties.getWindowSeconds(), TimeUnit.SECONDS);
            }
            if (count != null && count > properties.getRequestsPerMinute()) {
                rejectWithTooManyRequests(request, response);
                return;
            }
        } catch (Exception e) {
            log.warn("Rate limit check failed for ip={}, allowing request through: {}", ip, e.getMessage());
        }

        chain.doFilter(request, response);
    }

    private void rejectWithTooManyRequests(HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {
        int status = HttpStatus.TOO_MANY_REQUESTS.value();
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(RETRY_AFTER_HEADER, String.valueOf(properties.getWindowSeconds()));

        ErrorResponse body = new ErrorResponse(
                false,
                status,
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                RATE_LIMIT_ERROR_CODE,
                "Rate limit exceeded. Please retry after " + properties.getWindowSeconds() + " seconds.",
                List.of(),
                request.getRequestURI(),
                request.getHeader(TRACE_ID_HEADER),
                Instant.now().toString()
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}
