package com.company.scopery.modules.servicesupport.sla.domain.service;
import java.time.Duration; import java.time.Instant;
public final class SlaClockService {
    private SlaClockService(){}
    public static Instant dueAt(Instant startedAt, int resolveMinutes) {
        return startedAt.plus(Duration.ofMinutes(resolveMinutes));
    }
    public static boolean isBreached(Instant dueAt, Instant now) {
        return dueAt != null && now.isAfter(dueAt);
    }
    public static boolean isAtRisk(Instant dueAt, Instant now, int warnMinutes) {
        if (dueAt == null) return false;
        return !now.isAfter(dueAt) && Duration.between(now, dueAt).toMinutes() <= warnMinutes;
    }
}
