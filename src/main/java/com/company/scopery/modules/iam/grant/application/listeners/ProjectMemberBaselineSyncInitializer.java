package com.company.scopery.modules.iam.grant.application.listeners;

import com.company.scopery.modules.iam.grant.application.action.EnsureProjectMemberBaselineAccessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Backfills missing baseline actions (QUALITY_VIEW, TEST_VIEW, DEFECT_VIEW, RELEASE_VIEW, …)
 * onto every existing DIRECT ALLOW project-member grant.
 * Runs after ProjectIamBootstrapBackfillInitializer (@Order 42). Idempotent.
 */
@Component
@Order(43)
public class ProjectMemberBaselineSyncInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProjectMemberBaselineSyncInitializer.class);

    private final EnsureProjectMemberBaselineAccessAction ensureBaseline;

    public ProjectMemberBaselineSyncInitializer(EnsureProjectMemberBaselineAccessAction ensureBaseline) {
        this.ensureBaseline = ensureBaseline;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            int attached = ensureBaseline.syncAllExistingProjectMemberGrants();
            if (attached > 0) {
                log.info("[ProjectMemberBaselineSync] Backfill complete: attached={} missing actions", attached);
            }
        } catch (Exception ex) {
            log.warn("[ProjectMemberBaselineSync] failed: {}", ex.getMessage());
        }
    }
}
