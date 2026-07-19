package com.company.scopery.modules.project.gantt.application.response;

import java.util.List;

public record GanttExportResponse(String format, List<GanttExportTaskRowResponse> tasks) {}
