package com.company.scopery.modules.iam.grant.application.listeners;

import com.company.scopery.modules.iam.grant.application.action.SyncWorkspaceOwnerGrantActionsAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * After owner-policy catalog bump, sync missing actions onto existing OWNER grants.
 * Runs after {@code IamOwnerPolicyCatalogInitializer} (@Order 30) and alongside member baseline (@Order 40).
 */
@Component
@Order(41)
public class WorkspaceOwnerGrantSyncInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceOwnerGrantSyncInitializer.class);

    private final SyncWorkspaceOwnerGrantActionsAction syncOwnerGrants;

    public WorkspaceOwnerGrantSyncInitializer(SyncWorkspaceOwnerGrantActionsAction syncOwnerGrants) {
        this.syncOwnerGrants = syncOwnerGrants;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            int attached = syncOwnerGrants.executeAll();
            log.info("[WorkspaceOwnerGrantSync] complete attached={}", attached);
        } catch (Exception ex) {
            log.warn("[WorkspaceOwnerGrantSync] failed: {}", ex.getMessage());
        }
    }
}
