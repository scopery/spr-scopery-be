package com.company.scopery.modules.airecommendation.domain.value;

import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceLabel;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ConfidenceScoreTest {

    @ParameterizedTest
    @CsvSource({
        "1.0000, HIGH",
        "0.8500, HIGH",
        "0.9000, HIGH",
        "0.8499, MEDIUM",
        "0.6500, MEDIUM",
        "0.7000, MEDIUM",
        "0.6499, LOW",
        "0.4000, LOW",
        "0.5000, LOW",
        "0.3999, LOW",
        "0.0000, LOW",
    })
    void labelDerivation(String value, ConfidenceLabel expected) {
        ConfidenceScore score = ConfidenceScore.of(ConfidenceMethod.DETERMINISTIC, new BigDecimal(value));
        assertThat(score.label()).isEqualTo(expected);
    }

    @Test
    void isAboveMinimum_exactThreshold_true() {
        ConfidenceScore score = ConfidenceScore.of(ConfidenceMethod.DETERMINISTIC, new BigDecimal("0.4000"));
        assertThat(score.isAboveMinimum()).isTrue();
    }

    @Test
    void isAboveMinimum_belowThreshold_false() {
        ConfidenceScore score = ConfidenceScore.of(ConfidenceMethod.DETERMINISTIC, new BigDecimal("0.3999"));
        assertThat(score.isAboveMinimum()).isFalse();
    }

    @Test
    void isAboveMinimum_high_true() {
        ConfidenceScore score = ConfidenceScore.of(ConfidenceMethod.HEURISTIC, new BigDecimal("0.9500"));
        assertThat(score.isAboveMinimum()).isTrue();
    }

    @Test
    void of_preservesMethod() {
        ConfidenceScore score = ConfidenceScore.of(ConfidenceMethod.LLM, new BigDecimal("0.7500"));
        assertThat(score.method()).isEqualTo(ConfidenceMethod.LLM);
    }

    @Test
    void of_preservesValue() {
        BigDecimal value = new BigDecimal("0.7500");
        ConfidenceScore score = ConfidenceScore.of(ConfidenceMethod.LLM, value);
        assertThat(score.value()).isEqualByComparingTo(value);
    }
}
