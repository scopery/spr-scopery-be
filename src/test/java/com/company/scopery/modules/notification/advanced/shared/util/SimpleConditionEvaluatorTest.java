package com.company.scopery.modules.notification.advanced.shared.util;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleConditionEvaluatorTest {
    @Test void blankMatches() {
        assertThat(SimpleConditionEvaluator.matches(null, Map.of())).isTrue();
        assertThat(SimpleConditionEvaluator.matches("{}", Map.of())).isTrue();
    }

    @Test void equalsMatches() {
        assertThat(SimpleConditionEvaluator.matches("{\"equals\":{\"field\":\"status\",\"value\":\"ACTIVE\"}}",
                Map.of("status", "ACTIVE"))).isTrue();
        assertThat(SimpleConditionEvaluator.matches("{\"equals\":{\"field\":\"status\",\"value\":\"ACTIVE\"}}",
                Map.of("status", "INACTIVE"))).isFalse();
    }
}
