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
                new JobInfo("EmailOutboxProcessor", "notification/emailoutbox",
                        "${notification.email.outbox.fixed-delay-ms:10000}ms fixed delay",
                        "Process and send pending outbox emails"),
                new JobInfo("TransactionalOutboxProcessor", "common/outbox",
                        "${scopery.platform.outbox.fixed-delay-ms:5000}ms fixed delay",
                        "Claim and publish transactional platform outbox events"),
                new JobInfo("IdempotencyCleanupJob", "platform/idempotency",
                        "${scopery.platform.idempotency.cleanup-cron:0 15 * * * *}",
                        "Delete expired idempotency records"),
                new JobInfo("ProjectDueDateReminderJob", "projectnotification/reminder",
                        "${scopery.project-notification.reminder-cron:0 0 6 * * *}",
                        "Emit due-soon and overdue task reminder notifications"),
                new JobInfo("AdvancedReminderEvaluationJob", "notification/advanced/reminder",
                        "${scopery.advanced-notification.reminder-eval-cron:0 */15 * * * *}",
                        "Mark due advanced reminder instances as DISPATCHED (no email claim)"),
                new JobInfo("DigestEvaluationJob", "notification/advanced/digest",
                        "${scopery.advanced-notification.digest-eval-cron:0 0 * * * *}",
                        "Scan active digest rules (delivery via outbox only)"),
                new JobInfo("AlertEvaluationJob", "notification/advanced/alert",
                        "${scopery.advanced-notification.alert-eval-cron:0 */5 * * * *}",
                        "Scan active alert rules without claiming email delivery"),
                new JobInfo("RetentionPolicyScanJob", "trust/retention",
                        "${scopery.trust.retention.scan-cron:0 30 3 * * *}",
                        "Periodic retention dry-run tick (no data destruction)"),
                new JobInfo("ResourceOverloadEvaluationJob", "resourcecapacity/workload",
                        "${scopery.resource-capacity.overload-eval-cron:0 */10 * * * *}",
                        "Scan overload snapshots; fan-out in-app NotificationItems + email trigger/outbox path"),
                new JobInfo("SlaBreachEvaluationJob", "servicesupport/sla",
                        "${scopery.support.sla.breach-eval-cron:0 */5 * * * *}",
                        "Scan open SLA clocks for breaches and record breach rows"),
                new JobInfo("ProviderSyncStubJob", "integrationhub/sync",
                        "${scopery.integration.provider-sync-stub-cron:0 20 * * * *}",
                        "Stub sync for ACTIVE Slack/Drive/Jira connections; persists SyncRun only"),
                new JobInfo("ReportSnapshotRetentionJob", "reporting/snapshot",
                        "${scopery.reporting.snapshot-retention-cron:0 45 2 * * *}",
                        "Hard-delete report_snapshot rows older than retention-days"),
                new JobInfo("AiAssistantStreamEventPurgeJob", "aiassistant/jobs",
                        "${scopery.ai-assistant.jobs.stream-event-purge-cron:0 0 * * * *}",
                        "Delete expired aiassistant_stream_event rows (24h TTL)"),
                new JobInfo("AiAssistantRetentionJob", "aiassistant/jobs",
                        "${scopery.ai-assistant.jobs.retention-cron:0 0 3 * * *}",
                        "Soft-delete expired conversations and log purge candidates"),
                new JobInfo("AiAssistantStaleMessageRecoveryJob", "aiassistant/jobs",
                        "${scopery.ai-assistant.jobs.stale-recovery-fixed-delay-ms:300000}ms fixed delay",
                        "Mark QUEUED/CONTEXTUALIZING messages stuck >10 min as FAILED"),
                new JobInfo("AiAssistantHeartbeatJob", "aiassistant/jobs",
                        "${scopery.ai-assistant.jobs.heartbeat-fixed-delay-ms:15000}ms fixed delay",
                        "Emit SSE heartbeat to all active AI assistant stream emitters"),
                new JobInfo("RecommendationExpiryJob", "airecommendation/jobs",
                        "${scopery.ai.recommendation.expiry-cron:0 0 * * * *}",
                        "Mark active AI recommendations past their expires_at as EXPIRED (batch 500)")
        );
    }

    public record JobInfo(
            String jobClass,
            String module,
            String schedule,
            String description) {}
}
