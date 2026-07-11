package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UsageWindowCalculatorTest {

    private final UsageWindowCalculator calculator = new UsageWindowCalculator();

    // 2026-01-15T10:30:45Z used as a mid-period reference point for all tests
    private final Instant ref = Instant.parse("2026-01-15T10:30:45Z");

    @Test
    void calculateWindow_minute_truncatesToCurrentMinute() {
        UsageWindow window = calculator.calculateWindow(UsagePolicyPeriod.MINUTE, ref);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-01-15T10:30:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2026-01-15T10:31:00Z"));
    }

    @Test
    void calculateWindow_hour_truncatesToCurrentHour() {
        UsageWindow window = calculator.calculateWindow(UsagePolicyPeriod.HOUR, ref);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-01-15T10:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2026-01-15T11:00:00Z"));
    }

    @Test
    void calculateWindow_day_truncatesToCurrentDay() {
        UsageWindow window = calculator.calculateWindow(UsagePolicyPeriod.DAY, ref);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-01-15T00:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2026-01-16T00:00:00Z"));
    }

    @Test
    void calculateWindow_month_truncatesToFirstOfMonth() {
        UsageWindow window = calculator.calculateWindow(UsagePolicyPeriod.MONTH, ref);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2026-02-01T00:00:00Z"));
    }
}
