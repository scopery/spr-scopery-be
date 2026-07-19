package com.company.scopery.modules.raid.raiditem.domain;

import com.company.scopery.modules.raid.raiditem.domain.enums.RaidImpact;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidProbability;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskScoreCalculatorTest {

    @Test
    void score_multipliesProbabilityAndImpactWeights() {
        assertThat(RiskScoreCalculator.score(RaidProbability.LOW, RaidImpact.LOW)).isEqualTo(1);
        assertThat(RiskScoreCalculator.score(RaidProbability.MEDIUM, RaidImpact.HIGH)).isEqualTo(6);
        assertThat(RiskScoreCalculator.score(RaidProbability.VERY_HIGH, RaidImpact.CRITICAL)).isEqualTo(16);
    }

    @Test
    void score_returnsNullWhenEitherDimensionMissing() {
        assertThat(RiskScoreCalculator.score(null, RaidImpact.HIGH)).isNull();
        assertThat(RiskScoreCalculator.score(RaidProbability.HIGH, null)).isNull();
    }

    @Test
    void formulaVersion_isV1() {
        assertThat(RiskScoreCalculator.FORMULA_VERSION).isEqualTo("v1");
    }
}
