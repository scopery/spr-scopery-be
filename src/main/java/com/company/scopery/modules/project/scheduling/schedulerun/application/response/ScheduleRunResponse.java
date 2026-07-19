package com.company.scopery.modules.project.scheduling.schedulerun.application.response;

import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduleRunResponse(UUID id, UUID projectId, String status, String algorithmVersion,
        LocalDate planningStartDate, LocalDate planningEndDate, String resultSummaryJson,
        String errorCode, String errorMessage, Instant startedAt, Instant completedAt, Instant createdAt) {
    public static ScheduleRunResponse from(ScheduleRun r) {
        return new ScheduleRunResponse(r.id(),r.projectId(),r.status().name(),r.algorithmVersion(),
                r.planningStartDate(),r.planningEndDate(),r.resultSummaryJson(),r.errorCode(),r.errorMessage(),
                r.startedAt(),r.completedAt(),r.createdAt());
    }
}
