package com.company.scopery.platform.web.idempotency;

import com.company.scopery.common.privacy.SensitiveDataRedactor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyKeyFilterTest {

    @Mock private IdempotencyKeyJpaRepository repository;

    private IdempotencyKeyFilter filter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        filter = new IdempotencyKeyFilter(
                repository, new SensitiveDataRedactor(objectMapper), objectMapper, 24, 120, false,
                "/api/projects/*/tasks,/api/projects/*/scope-packages,/api/projects/*/quotes");
    }

    @Test
    void sameKeySameBody_replaysCompletedResponse() throws Exception {
        MockHttpServletRequest request = post("/api/workspaces", "{\"name\":\"A\"}");
        request.addHeader(IdempotencyKeyFilter.HEADER, "key-1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        IdempotencyKeyJpaEntity cached = new IdempotencyKeyJpaEntity();
        cached.setKeyHash("ignored");
        cached.setRequestHash(shaOf(request.getContentAsByteArray()));
        cached.setStatus(IdempotencyKeyJpaEntity.STATUS_COMPLETED);
        cached.setResponseStatus(201);
        cached.setResponseBody("{\"success\":true}");
        cached.setContentType("application/json");
        cached.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));

        when(repository.findByKeyHashAndExpiresAtAfter(any(), any())).thenReturn(Optional.of(cached));

        filter.doFilter(request, response, (req, res) -> {
            throw new IllegalStateException("should not execute chain");
        });

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getHeader("Idempotency-Replayed")).isEqualTo("true");
        assertThat(response.getContentAsString()).isEqualTo("{\"success\":true}");
    }

    @Test
    void sameKeyDifferentBody_returnsConflict() throws Exception {
        MockHttpServletRequest request = post("/api/workspaces", "{\"name\":\"B\"}");
        request.addHeader(IdempotencyKeyFilter.HEADER, "key-1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        IdempotencyKeyJpaEntity cached = new IdempotencyKeyJpaEntity();
        cached.setRequestHash("different-hash");
        cached.setStatus(IdempotencyKeyJpaEntity.STATUS_COMPLETED);
        cached.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
        when(repository.findByKeyHashAndExpiresAtAfter(any(), any())).thenReturn(Optional.of(cached));

        filter.doFilter(request, response, (req, res) -> {});

        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getContentAsString()).contains("PLATFORM_IDEMPOTENCY_CONFLICT");
    }

    @Test
    void withoutKey_filterSkipped() throws Exception {
        MockHttpServletRequest request = post("/api/workspaces", "{}");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        assertThat(filter.shouldNotFilter(request)).isTrue();
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void firstRequest_storesCompletedResponseWithoutSecrets() throws Exception {
        MockHttpServletRequest request = post("/api/workspaces", "{\"name\":\"A\",\"password\":\"secret\"}");
        request.addHeader(IdempotencyKeyFilter.HEADER, "key-2");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(repository.findByKeyHashAndExpiresAtAfter(any(), any())).thenReturn(Optional.empty());
        when(repository.findById(any())).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        filter.doFilter(request, response, (req, res) -> {
            HttpServletResponse http = (HttpServletResponse) res;
            http.setStatus(201);
            http.setContentType("application/json");
            http.getOutputStream().write("{\"token\":\"abc\",\"ok\":true}".getBytes(StandardCharsets.UTF_8));
        });

        ArgumentCaptor<IdempotencyKeyJpaEntity> captor = ArgumentCaptor.forClass(IdempotencyKeyJpaEntity.class);
        verify(repository, atLeastOnce()).saveAndFlush(captor.capture());
        IdempotencyKeyJpaEntity completed = captor.getAllValues().get(captor.getAllValues().size() - 1);
        assertThat(completed.getStatus()).isEqualTo(IdempotencyKeyJpaEntity.STATUS_COMPLETED);
        assertThat(completed.getResponseBody()).doesNotContain("abc");
        assertThat(completed.getResponseBody()).contains("[REDACTED]");
    }

    private MockHttpServletRequest post(String path, String body) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setContent(body.getBytes(StandardCharsets.UTF_8));
        request.setContentType("application/json");
        return request;
    }

    private String shaOf(byte[] body) throws Exception {
        return java.util.HexFormat.of().formatHex(
                java.security.MessageDigest.getInstance("SHA-256").digest(body));
    }
}
