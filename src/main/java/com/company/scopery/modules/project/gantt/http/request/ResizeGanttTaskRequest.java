package com.company.scopery.modules.project.gantt.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ResizeGanttTaskRequest(
        @NotNull LocalDate manualFinishDate,
        @NotBlank String reason,
        Boolean recalculate
) {}
