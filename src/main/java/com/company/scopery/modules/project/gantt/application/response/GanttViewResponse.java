package com.company.scopery.modules.project.gantt.application.response;

import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;

import java.util.List;

public record GanttViewResponse(
        ProjectResponse project,
        ScheduleRunResponse scheduleRun,
        List<GanttItemResponse> items,
        List<GanttDependencyResponse> dependencies,
        List<GanttIssueResponse> issues,
        GanttSummaryResponse summary
) {}
