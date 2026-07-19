package com.company.scopery.modules.projectnotification.reminder.application.response;

import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderRun;

import java.time.Instant;
import java.util.UUID;

public record ProjectReminderRunResponse(
        UUID id, UUID workspaceId, String runType, String status,
        Instant startedAt, Instant completedAt, String resultSummaryJson,
        String errorCode, String errorMessage, String traceId
) {
    public static ProjectReminderRunResponse from(ProjectReminderRun r) {
        return new ProjectReminderRunResponse(
                r.id(), r.workspaceId(), r.runType().name(), r.status().name(),
                r.startedAt(), r.completedAt(), r.resultSummaryJson(),
                r.errorCode(), r.errorMessage(), r.traceId());
    }
}
