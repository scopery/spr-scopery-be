package com.company.scopery.modules.project.gantt.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record ResizeGanttTaskCommand(
        UUID projectId,
        UUID taskId,
        LocalDate manualFinishDate,
        String reason,
        boolean recalculate
) {}
