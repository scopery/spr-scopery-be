package com.company.scopery.modules.reporting.snapshot.application.jobs;

import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class ReportSnapshotRetentionJob {

    private static final Logger log = LoggerFactory.getLogger(ReportSnapshotRetentionJob.class);

    private final ReportSnapshotRepository snapshots;
    private final int retentionDays;
    private final boolean enabled;

    public ReportSnapshotRetentionJob(ReportSnapshotRepository snapshots,
                                      @Value("${scopery.reporting.snapshot-retention-days:90}") int retentionDays,
                                      @Value("${scopery.reporting.snapshot-retention-enabled:true}") boolean enabled) {
        this.snapshots = snapshots;
        this.retentionDays = retentionDays;
        this.enabled = enabled;
    }

    @Scheduled(cron = "${scopery.reporting.snapshot-retention-cron:0 45 2 * * *}")
    @Transactional
    public void purgeExpiredSnapshots() {
        if (!enabled || retentionDays <= 0) {
            return;
        }
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        int deleted = snapshots.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Report snapshot retention deleted {} snapshot(s) older than {} days", deleted, retentionDays);
        }
    }
}
