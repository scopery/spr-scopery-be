package com.company.scopery.modules.airecommendation.domain.policy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SuppressionPolicyTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 30, 90})
    void validateDuration_validValues_noException(int days) {
        assertThatCode(() -> SuppressionPolicy.validateDuration(days)).doesNotThrowAnyException();
    }

    @Test
    void validateDuration_exactMax_noException() {
        assertThatCode(() -> SuppressionPolicy.validateDuration(SuppressionPolicy.MAX_DURATION_DAYS))
                .doesNotThrowAnyException();
    }

    @Test
    void validateDuration_exceedsMax_throws() {
        assertThatThrownBy(() -> SuppressionPolicy.validateDuration(91))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("90");
    }

    @Test
    void validateDuration_zero_throws() {
        assertThatThrownBy(() -> SuppressionPolicy.validateDuration(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateDuration_negative_throws() {
        assertThatThrownBy(() -> SuppressionPolicy.validateDuration(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void maxDurationDays_is90() {
        assertThatCode(() -> {
            assert SuppressionPolicy.MAX_DURATION_DAYS == 90;
        }).doesNotThrowAnyException();
    }
}
