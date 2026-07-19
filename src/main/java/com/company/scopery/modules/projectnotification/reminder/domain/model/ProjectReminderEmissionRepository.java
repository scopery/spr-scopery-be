package com.company.scopery.modules.projectnotification.reminder.domain.model;

import java.util.UUID;

public interface ProjectReminderEmissionRepository {
    ProjectReminderEmission save(ProjectReminderEmission emission);
    boolean existsByDedupKey(String dedupKey);
}
