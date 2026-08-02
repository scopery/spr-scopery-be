package com.company.scopery.modules.project.timeline.domain.rules;

import com.company.scopery.modules.project.timeline.domain.enums.TimelineGranularity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Pure timeline bucket math. Never invents Actual from Start–End alone.
 */
public final class TimelineBucketCalculator {

    private static final int DEFAULT_DAY_MINUTES = 8 * 60;

    private TimelineBucketCalculator() {}

    public record Period(LocalDate periodStart, LocalDate periodEnd) {}

    public record ProgressPoint(LocalDate snapshotDate, BigDecimal progressPercent) {}

    public record BucketMetrics(
            LocalDate periodStart,
            LocalDate periodEnd,
            int plannedMinutes,
            BigDecimal plannedContributionPercent,
            BigDecimal cumulativePlannedPercent,
            BigDecimal actualProgressPercent,
            BigDecimal variancePercent
    ) {}

    public static List<Period> buildPeriods(LocalDate from, LocalDate to, TimelineGranularity granularity) {
        if (from == null || to == null || to.isBefore(from)) {
            return List.of();
        }
        return switch (granularity) {
            case DAY -> buildDayPeriods(from, to);
            case WEEK -> buildWeekPeriods(from, to);
            case MONTH -> buildMonthPeriods(from, to);
            case QUARTER -> buildQuarterPeriods(from, to);
        };
    }

    /**
     * Evenly split estimate across Mon–Fri working days in [start, end].
     * Remainder minutes are distributed one-by-one from the start.
     */
    public static Map<LocalDate, Integer> autoDailyAllocationMinutes(
            LocalDate start,
            LocalDate end,
            Integer estimateMinutes) {
        Map<LocalDate, Integer> map = new LinkedHashMap<>();
        if (start == null || end == null || end.isBefore(start)) {
            return map;
        }
        List<LocalDate> working = workingDays(start, end);
        if (working.isEmpty()) {
            return map;
        }
        int total = estimateMinutes != null && estimateMinutes > 0
                ? estimateMinutes
                : working.size() * DEFAULT_DAY_MINUTES;
        int base = total / working.size();
        int remainder = total - base * working.size();
        for (LocalDate day : working) {
            int extra = remainder > 0 ? 1 : 0;
            if (remainder > 0) {
                remainder -= 1;
            }
            map.put(day, base + extra);
        }
        return map;
    }

    /**
     * Prefer non-empty manual allocations; otherwise AUTO split.
     */
    public static Map<LocalDate, Integer> resolveDailyAllocationMinutes(
            LocalDate start,
            LocalDate end,
            Integer estimateMinutes,
            Map<LocalDate, Integer> manualByDate) {
        if (manualByDate != null && !manualByDate.isEmpty()) {
            Map<LocalDate, Integer> map = new LinkedHashMap<>();
            manualByDate.forEach((day, minutes) -> {
                if (minutes != null && minutes > 0) {
                    map.put(day, minutes);
                }
            });
            if (!map.isEmpty()) {
                return map;
            }
        }
        return autoDailyAllocationMinutes(start, end, estimateMinutes);
    }

    public static List<BucketMetrics> buildBuckets(
            List<Period> periods,
            LocalDate start,
            LocalDate end,
            Integer estimateMinutes,
            Map<LocalDate, Integer> dailyAllocation,
            List<ProgressPoint> snapshots) {
        if (periods.isEmpty()) {
            return List.of();
        }
        if (start == null || end == null) {
            return periods.stream()
                    .map(p -> new BucketMetrics(p.periodStart(), p.periodEnd(), 0, null, null, null, null))
                    .toList();
        }

        Map<LocalDate, Integer> daily = dailyAllocation != null ? dailyAllocation : Map.of();
        int totalPlanned = daily.values().stream().mapToInt(Integer::intValue).sum();
        boolean hasEstimate = estimateMinutes != null && estimateMinutes > 0 && totalPlanned > 0;

        NavigableMap<LocalDate, BigDecimal> progressByDate = new TreeMap<>();
        if (snapshots != null) {
            snapshots.stream()
                    .filter(s -> s.snapshotDate() != null && s.progressPercent() != null)
                    .sorted(Comparator.comparing(ProgressPoint::snapshotDate))
                    .forEach(s -> progressByDate.put(s.snapshotDate(), s.progressPercent()));
        }

        int cumulative = 0;
        List<BucketMetrics> out = new ArrayList<>(periods.size());
        for (Period period : periods) {
            int plannedMinutes = 0;
            for (Map.Entry<LocalDate, Integer> e : daily.entrySet()) {
                LocalDate day = e.getKey();
                if (!day.isBefore(period.periodStart()) && !day.isAfter(period.periodEnd())) {
                    plannedMinutes += e.getValue();
                }
            }
            cumulative += plannedMinutes;

            BigDecimal contribution = null;
            BigDecimal cumulativePct = null;
            if (hasEstimate) {
                contribution = round1((plannedMinutes * 100.0) / totalPlanned);
                cumulativePct = round1((cumulative * 100.0) / totalPlanned);
            }

            boolean scheduled = rangesOverlap(start, end, period.periodStart(), period.periodEnd());
            BigDecimal actual = null;
            if (scheduled) {
                actual = resolveActualAsOf(progressByDate, period.periodEnd());
            }
            BigDecimal variance = null;
            if (actual != null && cumulativePct != null) {
                variance = round1(actual.doubleValue() - cumulativePct.doubleValue());
            }

            out.add(new BucketMetrics(
                    period.periodStart(),
                    period.periodEnd(),
                    plannedMinutes,
                    contribution,
                    cumulativePct,
                    actual,
                    variance));
        }
        return out;
    }

    public static BigDecimal resolveActualAsOf(
            NavigableMap<LocalDate, BigDecimal> progressByDate,
            LocalDate asOf) {
        if (progressByDate == null || progressByDate.isEmpty() || asOf == null) {
            return null;
        }
        Map.Entry<LocalDate, BigDecimal> floor = progressByDate.floorEntry(asOf);
        return floor != null ? floor.getValue() : null;
    }

    public static List<LocalDate> workingDays(LocalDate start, LocalDate end) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            DayOfWeek dow = cursor.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                days.add(cursor);
            }
            cursor = cursor.plusDays(1);
        }
        return days;
    }

    public static boolean rangesOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return !aStart.isAfter(bEnd) && !bStart.isAfter(aEnd);
    }

    private static List<Period> buildDayPeriods(LocalDate from, LocalDate to) {
        List<Period> periods = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            periods.add(new Period(cursor, cursor));
            cursor = cursor.plusDays(1);
        }
        return periods;
    }

    private static List<Period> buildWeekPeriods(LocalDate from, LocalDate to) {
        List<Period> periods = new ArrayList<>();
        LocalDate cursor = from.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        while (!cursor.isAfter(to)) {
            LocalDate weekEnd = cursor.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            periods.add(new Period(cursor, weekEnd));
            cursor = weekEnd.plusDays(1);
        }
        return periods;
    }

    private static List<Period> buildMonthPeriods(LocalDate from, LocalDate to) {
        List<Period> periods = new ArrayList<>();
        LocalDate cursor = from.withDayOfMonth(1);
        while (!cursor.isAfter(to)) {
            LocalDate monthEnd = cursor.with(TemporalAdjusters.lastDayOfMonth());
            periods.add(new Period(cursor, monthEnd));
            cursor = monthEnd.plusDays(1);
        }
        return periods;
    }

    private static List<Period> buildQuarterPeriods(LocalDate from, LocalDate to) {
        List<Period> periods = new ArrayList<>();
        int startMonth = ((from.getMonthValue() - 1) / 3) * 3 + 1;
        LocalDate cursor = LocalDate.of(from.getYear(), startMonth, 1);
        while (!cursor.isAfter(to)) {
            LocalDate quarterEnd = cursor.plusMonths(3).minusDays(1);
            periods.add(new Period(cursor, quarterEnd));
            cursor = quarterEnd.plusDays(1);
        }
        return periods;
    }

    private static BigDecimal round1(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }
}
