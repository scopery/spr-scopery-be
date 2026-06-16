package com.company.scopery.modules.aiagent.execution.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ExecutionRequestIdTest {

    @Test
    void of_validInput_returnsNormalized() {
        ExecutionRequestId id = ExecutionRequestId.of("  req-abc-123  ");
        assertThat(id.value()).isEqualTo("req-abc-123");
    }

    @Test
    void of_uuidFormat_accepted() {
        ExecutionRequestId id = ExecutionRequestId.of("550e8400-e29b-41d4-a716-446655440000");
        assertThat(id.value()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
    }

    @Test
    void of_blank_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ExecutionRequestId.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void of_null_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ExecutionRequestId.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_exceedsMaxLength_throwsIllegalArgumentException() {
        String tooLong = "a".repeat(151);
        assertThatThrownBy(() -> ExecutionRequestId.of(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("150");
    }

    @Test
    void of_exactMaxLength_accepted() {
        String maxLen = "a".repeat(150);
        ExecutionRequestId id = ExecutionRequestId.of(maxLen);
        assertThat(id.value()).hasSize(150);
    }

    @Test
    void equalityByValue() {
        ExecutionRequestId a = ExecutionRequestId.of("req-123");
        ExecutionRequestId b = ExecutionRequestId.of("req-123");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
