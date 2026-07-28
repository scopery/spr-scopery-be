package com.company.scopery.modules.workspace.shared.listeners;

import com.company.scopery.modules.workspace.shared.service.InvitationInboxCleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dismiss leftover invitation notifications / work-inbox items for invites already
 * accepted, cancelled, revoked, or expired (created before accept-path cleanup existed).
 */
@Component
@Order(55)
public class InvitationInboxBackfillInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(InvitationInboxBackfillInitializer.class);

    private final InvitationInboxCleanupService cleanupService;

    public InvitationInboxBackfillInitializer(InvitationInboxCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            int[] counts = cleanupService.backfillStaleInvitationInbox();
            log.info("[InvitationInboxBackfill] dismissed notifications={}, workInbox={}",
                    counts[0], counts[1]);
        } catch (Exception ex) {
            log.warn("[InvitationInboxBackfill] failed: {}", ex.getMessage());
        }
    }
}
