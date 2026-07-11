package com.company.scopery.platform.scheduler;

import java.util.List;

/**
 * Central catalog of every @Scheduled job in the system.
 * When adding a new job class, add an entry here so the whole team can see
 * what background work is running and on what schedule.
 */
public final class ScheduledJobRegistry {

    private ScheduledJobRegistry() {}

    public static List<JobInfo> all() {
        return List.of(
                new JobInfo("EmailOutboxProcessor", "notification/emailoutbox", "${notification.email.outbox.fixed-delay-ms:10000}ms fixed delay", "Process and send pending outbox emails")
        );
    }

    public record JobInfo(
            String jobClass,
            String module,
            String schedule,
            String description) {}
}
