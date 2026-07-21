package com.company.scopery.modules.project.gantt.application.service;

import com.company.scopery.modules.project.gantt.domain.enums.GanttItemScheduleStatus;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleRiskStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.model.Task;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GanttTaskDateResolverTest {

    @Test
    void pinRangeOverridesScheduleDates() {
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        Task task = Task.create(projectId, UUID.randomUUID(), null, "T1", "Task", null,
                UUID.randomUUID(), null, null, new BigDecimal("4"), null, null, TaskPriority.MEDIUM);
        TaskSchedule schedule = TaskSchedule.create(UUID.randomUUID(), projectId, taskId, null, null,
                LocalDate.of(2026, 8, 2), LocalDate.of(2026, 8, 3),
                new BigDecimal("4"), BigDecimal.ZERO, null, BigDecimal.ZERO,
                TaskScheduleRiskStatus.ON_TRACK, TaskScheduleStatus.SCHEDULED);
        TaskScheduleOverride override = TaskScheduleOverride.create(
                projectId, taskId, ScheduleOverrideType.PIN_RANGE,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10), null, "drag");

        GanttTaskDateResolver.EffectiveTaskDates effective =
                GanttTaskDateResolver.resolve(task, schedule, override);

        assertThat(effective.start()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(effective.end()).isEqualTo(LocalDate.of(2026, 9, 10));
        assertThat(GanttTaskDateResolver.resolveDisplayStatus(schedule, effective))
                .isEqualTo(GanttItemScheduleStatus.SCHEDULED);
    }
}
