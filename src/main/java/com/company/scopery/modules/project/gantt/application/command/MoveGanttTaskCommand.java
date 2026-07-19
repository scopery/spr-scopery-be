package com.company.scopery.modules.project.gantt.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record MoveGanttTaskCommand(
        UUID projectId,
        UUID taskId,
        LocalDate manualStartDate,
        LocalDate manualFinishDate,
        String reason,
        boolean recalculate
) {}
