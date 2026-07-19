package com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduledDailyWorkRepository {
    List<ScheduledDailyWork> saveAll(List<ScheduledDailyWork> work);
    List<ScheduledDailyWork> findAllByScheduleRunId(UUID runId);
    List<ScheduledDailyWork> findAllByScheduleRunIdAndDateRange(UUID runId, LocalDate from, LocalDate to);
}
