package com.company.scopery.modules.project.gantt.http.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MoveGanttTaskRequest(
        LocalDate manualStartDate,
        LocalDate manualFinishDate,
        @NotBlank String reason,
        Boolean recalculate
) {}
