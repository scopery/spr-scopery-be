package com.company.scopery.common.privacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveDataRedactorTest {

    private SensitiveDataRedactor redactor;

    @BeforeEach
    void setUp() {
        redactor = new SensitiveDataRedactor(new ObjectMapper());
    }

    @Test
    void redact_removesPasswordAndTokenFields() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("username", "alice");
        payload.put("password", "Secret123!");
        payload.put("accessToken", "jwt-token");
        payload.put("nested", Map.of("apiKey", "sk-live", "email", "a@example.com"));

        @SuppressWarnings("unchecked")
        Map<String, Object> redacted = (Map<String, Object>) redactor.redact(payload);

        assertThat(redacted.get("username")).isEqualTo("alice");
        assertThat(redacted.get("password")).isEqualTo(SensitiveDataRedactor.REDACTED);
        assertThat(redacted.get("accessToken")).isEqualTo(SensitiveDataRedactor.REDACTED);
        @SuppressWarnings("unchecked")
        Map<String, Object> nested = (Map<String, Object>) redacted.get("nested");
        assertThat(nested.get("apiKey")).isEqualTo(SensitiveDataRedactor.REDACTED);
        assertThat(nested.get("email")).isEqualTo("a@example.com");
    }

    @Test
    void redactJson_masksBearerTokens() {
        String json = "{\"authorization\":\"Bearer abc.def.ghi\",\"ok\":true}";
        String redacted = redactor.redactJson(json);
        assertThat(redacted).doesNotContain("abc.def.ghi");
        assertThat(redacted).contains(SensitiveDataRedactor.REDACTED);
    }
}
