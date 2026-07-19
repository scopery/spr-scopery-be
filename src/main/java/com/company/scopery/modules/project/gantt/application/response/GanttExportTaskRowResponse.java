package com.company.scopery.modules.project.gantt.application.response;

import java.time.LocalDate;
import java.util.UUID;

public record GanttExportTaskRowResponse(
        UUID taskId,
        String title,
        String scheduleStatus,
        LocalDate startDate,
        LocalDate finishDate,
        UUID projectPhaseId,
        UUID wbsNodeId,
        UUID inChargeUserId
) {}
