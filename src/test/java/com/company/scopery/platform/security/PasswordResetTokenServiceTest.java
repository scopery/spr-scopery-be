package com.company.scopery.platform.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    private PasswordResetTokenService service;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new PasswordResetTokenService(redisTemplate);
    }

    @Test
    void create_storesSha256HashNotRawToken() {
        UUID userId = UUID.randomUUID();
        String raw = service.create(userId);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(keyCaptor.capture(), eq(userId.toString()), eq(30L), eq(TimeUnit.MINUTES));

        String key = keyCaptor.getValue();
        assertThat(key).startsWith("scopery:password_reset:");
        assertThat(key).doesNotContain(raw);
        assertThat(key).endsWith(PasswordResetTokenService.sha256(raw));
    }

    @Test
    void consume_readsHashedKeyAndDeletes() {
        UUID userId = UUID.randomUUID();
        String raw = "abc123token";
        String key = "scopery:password_reset:" + PasswordResetTokenService.sha256(raw);
        when(valueOperations.get(key)).thenReturn(userId.toString());

        Optional<UUID> result = service.consume(raw);

        assertThat(result).contains(userId);
        verify(redisTemplate).delete(key);
    }

    @Test
    void consume_blankToken_returnsEmpty() {
        assertThat(service.consume("  ")).isEmpty();
        verify(valueOperations, never()).get(anyString());
    }
}
