package com.company.scopery.modules.project.gantt.application.response;

import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;

public record GanttRecalculateResponse(
        ScheduleRunResponse scheduleRun,
        GanttSummaryResponse summary
) {}
