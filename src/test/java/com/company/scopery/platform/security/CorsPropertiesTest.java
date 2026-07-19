package com.company.scopery.platform.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorsPropertiesTest {

    @Test
    void validate_allowsExplicitOriginsWithCredentials() {
        CorsProperties props = new CorsProperties();
        props.setAllowCredentials(true);
        props.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));

        assertThatCode(props::validateForCredentialedRequests).doesNotThrowAnyException();
    }

    @Test
    void validate_rejectsWildcardWithCredentials() {
        CorsProperties props = new CorsProperties();
        props.setAllowCredentials(true);
        props.setAllowedOrigins(List.of("*"));

        assertThatThrownBy(props::validateForCredentialedRequests)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not contain '*'");
    }

    @Test
    void validate_rejectsEmptyOriginsWithCredentials() {
        CorsProperties props = new CorsProperties();
        props.setAllowCredentials(true);
        props.setAllowedOrigins(List.of());

        assertThatThrownBy(props::validateForCredentialedRequests)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must be configured");
    }
}
