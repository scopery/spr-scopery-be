package com.company.scopery.modules.project.timeline.domain.rules;

import com.company.scopery.modules.project.timeline.domain.enums.TimelineGranularity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TimelineBucketCalculatorTest {

    @Test
    void splitsFortyHoursAcrossMondayToFriday() {
        LocalDate start = LocalDate.of(2026, 8, 3);
        LocalDate end = LocalDate.of(2026, 8, 7);
        Map<LocalDate, Integer> daily = TimelineBucketCalculator.autoDailyAllocationMinutes(start, end, 40 * 60);

        assertThat(daily.values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(2400);
        assertThat(daily.get(LocalDate.of(2026, 8, 3))).isEqualTo(480);
        assertThat(daily).doesNotContainKey(LocalDate.of(2026, 8, 8));
    }

    @Test
    void cumulativePlannedIsSixtyPercentOnWednesday() {
        LocalDate start = LocalDate.of(2026, 8, 3);
        LocalDate end = LocalDate.of(2026, 8, 7);
        List<TimelineBucketCalculator.Period> periods =
                TimelineBucketCalculator.buildPeriods(start, end, TimelineGranularity.DAY);
        Map<LocalDate, Integer> daily =
                TimelineBucketCalculator.autoDailyAllocationMinutes(start, end, 40 * 60);

        List<TimelineBucketCalculator.BucketMetrics> buckets = TimelineBucketCalculator.buildBuckets(
                periods, start, end, 40 * 60, daily, List.of());

        assertThat(buckets).hasSize(5);
        assertThat(buckets.get(0).plannedContributionPercent()).isEqualByComparingTo("20.0");
        assertThat(buckets.get(2).cumulativePlannedPercent()).isEqualByComparingTo("60.0");
        assertThat(buckets.get(4).cumulativePlannedPercent()).isEqualByComparingTo("100.0");
        assertThat(buckets.get(2).actualProgressPercent()).isNull();
    }

    @Test
    void actualUsesLastSnapshotOnOrBeforeBucketEnd() {
        LocalDate start = LocalDate.of(2026, 8, 3);
        LocalDate end = LocalDate.of(2026, 8, 7);
        List<TimelineBucketCalculator.Period> periods =
                TimelineBucketCalculator.buildPeriods(start, end, TimelineGranularity.DAY);
        Map<LocalDate, Integer> daily =
                TimelineBucketCalculator.autoDailyAllocationMinutes(start, end, 40 * 60);

        List<TimelineBucketCalculator.BucketMetrics> buckets = TimelineBucketCalculator.buildBuckets(
                periods,
                start,
                end,
                40 * 60,
                daily,
                List.of(new TimelineBucketCalculator.ProgressPoint(
                        LocalDate.of(2026, 8, 5), BigDecimal.valueOf(35))));

        assertThat(buckets.get(1).actualProgressPercent()).isNull();
        assertThat(buckets.get(2).actualProgressPercent()).isEqualByComparingTo("35");
        assertThat(buckets.get(4).actualProgressPercent()).isEqualByComparingTo("35");
        assertThat(buckets.get(2).variancePercent())
                .isEqualByComparingTo(BigDecimal.valueOf(35 - 60).setScale(1));
    }
}
