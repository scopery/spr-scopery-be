package com.company.scopery.modules.projectnotification.reminder.application.jobs;

import com.company.scopery.modules.projectnotification.reminder.application.service.ProjectDueDateReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProjectDueDateReminderJob {
    private static final Logger log = LoggerFactory.getLogger(ProjectDueDateReminderJob.class);
    private final ProjectDueDateReminderService reminderService;

    public ProjectDueDateReminderJob(ProjectDueDateReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(cron = "${scopery.project-notification.reminder-cron:0 0 6 * * *}")
    public void run() {
        try {
            var result = reminderService.runDaily(null);
            log.info("[ProjectDueDateReminderJob] Completed run {} status={}", result.id(), result.status());
        } catch (Exception e) {
            log.error("[ProjectDueDateReminderJob] Failed: {}", e.getMessage());
        }
    }
}
