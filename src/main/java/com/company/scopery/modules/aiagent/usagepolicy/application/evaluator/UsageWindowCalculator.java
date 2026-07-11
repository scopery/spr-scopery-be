package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class UsageWindowCalculator {

    // UTC is used consistently across all window calculations.
    private static final ZoneId UTC = ZoneId.of("UTC");

    public UsageWindow calculateWindow(UsagePolicyPeriod period, Instant currentTime) {
        ZonedDateTime now = currentTime.atZone(UTC);

        return switch (period) {
            case MINUTE -> {
                ZonedDateTime start = now.truncatedTo(ChronoUnit.MINUTES);
                yield new UsageWindow(start.toInstant(), start.plusMinutes(1).toInstant());
            }
            case HOUR -> {
                ZonedDateTime start = now.truncatedTo(ChronoUnit.HOURS);
                yield new UsageWindow(start.toInstant(), start.plusHours(1).toInstant());
            }
            case DAY -> {
                ZonedDateTime start = now.truncatedTo(ChronoUnit.DAYS);
                yield new UsageWindow(start.toInstant(), start.plusDays(1).toInstant());
            }
            case MONTH -> {
                ZonedDateTime start = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                yield new UsageWindow(start.toInstant(), start.plusMonths(1).toInstant());
            }
        };
    }
}
