package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.modules.project.gantt.application.command.RecalculateGanttCommand;
import com.company.scopery.modules.project.gantt.application.query.GanttViewQuery;
import com.company.scopery.modules.project.gantt.application.response.GanttRecalculateResponse;
import com.company.scopery.modules.project.gantt.application.response.GanttSummaryResponse;
import com.company.scopery.modules.project.gantt.application.response.GanttViewResponse;
import com.company.scopery.modules.project.gantt.application.service.GanttQueryService;
import com.company.scopery.modules.project.scheduling.schedulerun.application.action.CreateScheduleRunAction;
import com.company.scopery.modules.project.scheduling.schedulerun.application.command.CreateScheduleRunCommand;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecalculateGanttActionTest {

    @Mock ProjectWorkspaceAuthorizationService authorization;
    @Mock CreateScheduleRunAction createScheduleRunAction;
    @Mock GanttQueryService ganttQueryService;
    @Mock ProjectPlatformPublisher publisher;
    @Mock ProjectActivityLogger activityLogger;

    RecalculateGanttAction action;
    UUID projectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new RecalculateGanttAction(authorization, createScheduleRunAction, ganttQueryService,
                publisher, activityLogger);
    }

    @Test
    void delegatesToCreateScheduleRunAction() {
        ScheduleRunResponse run = new ScheduleRunResponse(UUID.randomUUID(), projectId, "COMPLETED", "1.0.0",
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31), "{}", null, null, null, null, null);
        when(createScheduleRunAction.execute(any())).thenReturn(run);
        when(ganttQueryService.getView(any())).thenReturn(new GanttViewResponse(
                null, run, List.of(), List.of(), List.of(),
                new GanttSummaryResponse(0, 0, 0, 0, 0, 0, 0)));

        GanttRecalculateResponse result = action.execute(new RecalculateGanttCommand(
                projectId, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31), false, true));

        ArgumentCaptor<CreateScheduleRunCommand> captor = ArgumentCaptor.forClass(CreateScheduleRunCommand.class);
        verify(createScheduleRunAction).execute(captor.capture());
        assertThat(captor.getValue().projectId()).isEqualTo(projectId);
        assertThat(result.scheduleRun().id()).isEqualTo(run.id());
        verify(publisher).enqueueGantt(eq(projectId), eq("GANTT_RECALCULATION_REQUESTED"), any());
        verify(publisher).enqueueGantt(eq(projectId), eq("GANTT_RECALCULATED"), any());
        verify(ganttQueryService).getView(any(GanttViewQuery.class));
    }
}
