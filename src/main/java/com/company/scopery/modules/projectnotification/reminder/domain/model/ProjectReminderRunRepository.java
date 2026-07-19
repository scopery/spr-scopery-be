package com.company.scopery.modules.projectnotification.reminder.domain.model;

import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderRunStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectReminderRunRepository {
    ProjectReminderRun save(ProjectReminderRun run);
    Optional<ProjectReminderRun> findById(UUID id);
    List<ProjectReminderRun> findRecent(int limit);
    boolean existsByStatus(ReminderRunStatus status);
}
