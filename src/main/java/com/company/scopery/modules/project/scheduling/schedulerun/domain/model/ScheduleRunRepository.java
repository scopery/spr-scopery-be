package com.company.scopery.modules.project.scheduling.schedulerun.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRunRepository {
    ScheduleRun save(ScheduleRun run);
    Optional<ScheduleRun> findById(UUID id);
    List<ScheduleRun> findAllByProjectId(UUID projectId);
    Optional<ScheduleRun> findCurrent(UUID projectId, UUID currentScheduleRunId);
}
