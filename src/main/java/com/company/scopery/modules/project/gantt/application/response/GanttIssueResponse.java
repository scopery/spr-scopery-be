package com.company.scopery.modules.project.gantt.application.response;

import java.time.LocalDate;
import java.util.UUID;

public record GanttIssueResponse(
        UUID id,
        UUID taskId,
        UUID userId,
        String issueType,
        String severity,
        String message,
        LocalDate issueDate
) {}
