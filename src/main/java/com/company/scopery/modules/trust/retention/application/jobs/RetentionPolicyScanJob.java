package com.company.scopery.modules.trust.retention.application.jobs;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component;
/**
 * Periodic dry-run retention scan placeholder. Does not destroy data.
 * Actual candidate discovery across modules is intentionally limited in Phase 38.
 */
@Component
public class RetentionPolicyScanJob {
    private static final Logger log = LoggerFactory.getLogger(RetentionPolicyScanJob.class);
    @Scheduled(cron = "${scopery.trust.retention.scan-cron:0 30 3 * * *}")
    public void scan() {
        log.info("Trust retention policy scan tick (dry-run only; no data destruction)");
    }
}
