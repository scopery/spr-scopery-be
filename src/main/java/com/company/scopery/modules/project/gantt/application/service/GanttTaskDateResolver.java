package com.company.scopery.modules.project.gantt.application.service;

import com.company.scopery.modules.project.gantt.domain.enums.GanttItemScheduleStatus;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.task.domain.model.Task;

import java.time.LocalDate;

/**
 * Merges {@link TaskSchedule} with active {@link TaskScheduleOverride} for Gantt display.
 * Move/resize APIs persist overrides; Gantt projection must reflect them even when
 * {@code recalculate=false} (no new schedule run).
 */
public final class GanttTaskDateResolver {

    private GanttTaskDateResolver() {
    }

    public record EffectiveTaskDates(LocalDate start, LocalDate end) {
    }

    public static EffectiveTaskDates resolve(
            Task task,
            TaskSchedule schedule,
            TaskScheduleOverride override) {
        LocalDate start = schedule != null ? schedule.estimatedStartDate() : null;
        LocalDate end = schedule != null ? schedule.estimatedFinishDate() : null;

        if (override != null) {
            switch (override.overrideType()) {
                case PIN_RANGE -> {
                    if (override.manualStartDate() != null) {
                        start = override.manualStartDate();
                    }
                    if (override.manualFinishDate() != null) {
                        end = override.manualFinishDate();
                    }
                }
                case PIN_START -> {
                    if (override.manualStartDate() != null) {
                        start = override.manualStartDate();
                    }
                }
                case PIN_FINISH -> {
                    if (override.manualFinishDate() != null) {
                        end = override.manualFinishDate();
                    }
                }
                case DUE_DATE_OVERRIDE -> {
                    // Due-date overrides do not move Gantt bars directly.
                }
            }
        }

        if (start == null) {
            start = task.plannedStartDate();
        }
        if (end == null) {
            end = task.dueDate();
        }

        return new EffectiveTaskDates(start, end);
    }

    public static GanttItemScheduleStatus resolveDisplayStatus(
            TaskSchedule schedule,
            EffectiveTaskDates effective) {
        if (effective.start() != null && effective.end() != null) {
            if (schedule != null && schedule.scheduleStatus() == TaskScheduleStatus.BLOCKED) {
                return GanttItemScheduleStatus.BLOCKED;
            }
            return GanttItemScheduleStatus.SCHEDULED;
        }
        if (effective.start() != null || effective.end() != null) {
            return GanttItemScheduleStatus.PARTIAL;
        }
        if (schedule == null) {
            return GanttItemScheduleStatus.UNSCHEDULED;
        }
        return switch (schedule.scheduleStatus()) {
            case SCHEDULED -> GanttItemScheduleStatus.SCHEDULED;
            case PARTIALLY_SCHEDULED -> GanttItemScheduleStatus.PARTIAL;
            case BLOCKED -> GanttItemScheduleStatus.BLOCKED;
            default -> GanttItemScheduleStatus.UNSCHEDULED;
        };
    }
}
