package com.company.scopery.modules.resourcecapacity.workingcalendar.application.listeners;

import com.company.scopery.modules.resourcecapacity.workingcalendar.application.service.DefaultWorkingCalendarSeeder;
import com.company.scopery.modules.workspace.workspace.application.event.WorkspaceCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bootstraps a default working calendar for every newly created workspace, once the workspace
 * creation transaction has committed. Kept as a listener (rather than a direct dependency inside
 * {@code CreateWorkspaceAction}) to avoid a compile-time dependency from workspace -> resourcecapacity.
 */
@Component
public class WorkspaceCreatedCapacitySeedListener {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceCreatedCapacitySeedListener.class);

    private final DefaultWorkingCalendarSeeder seeder;

    public WorkspaceCreatedCapacitySeedListener(DefaultWorkingCalendarSeeder seeder) {
        this.seeder = seeder;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onWorkspaceCreated(WorkspaceCreatedEvent event) {
        try {
            seeder.ensureDefaultCalendar(event.workspaceId());
        } catch (Exception e) {
            log.warn("Failed to seed default working calendar for workspace {}: {}",
                    event.workspaceId(), e.getMessage());
        }
    }
}
