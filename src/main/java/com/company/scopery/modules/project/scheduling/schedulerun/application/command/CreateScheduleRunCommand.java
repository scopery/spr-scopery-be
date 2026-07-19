package com.company.scopery.modules.project.scheduling.schedulerun.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateScheduleRunCommand(UUID projectId, LocalDate planningStartDate, LocalDate planningEndDate,
                                       boolean includeCompletedTasks, boolean markAsCurrent) {}
