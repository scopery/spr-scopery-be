package com.company.scopery.modules.project.scheduling.schedulerun.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduling.engine.ScheduleEngineService;
import com.company.scopery.modules.project.scheduling.schedulerun.application.command.CreateScheduleRunCommand;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateScheduleRunActionTest {

    @Mock private ProjectRepository projects;
    @Mock private ScheduleRunRepository runs;
    @Mock private TaskScheduleRepository taskSchedules;
    @Mock private ProjectWorkspaceAuthorizationService authorization;
    @Mock private CurrentUserAuthorizationService currentUser;
    @Mock private ScheduleEngineService engine;
    @Mock private ProjectPlatformPublisher publisher;
    @Mock private ProjectActivityLogger activityLogger;

    private CreateScheduleRunAction action;
    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateScheduleRunAction(projects, runs, taskSchedules, authorization, currentUser,
                engine, publisher, activityLogger);
    }

    @Test
    void createScheduleRun_archivedProject_rejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ARCHIVED)));

        assertThatThrownBy(() -> action.execute(command(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 10))))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException ae = (AppException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo(ProjectErrorCatalog.SCHEDULE_RUN_PROJECT_ARCHIVED.code());
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                });
    }

    @Test
    void createScheduleRun_invalidDateRange_rejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ACTIVE)));

        assertThatThrownBy(() -> action.execute(command(LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 1))))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.SCHEDULE_RUN_INVALID_DATE_RANGE.code()));
    }

    @Test
    void createScheduleRun_rangeTooLarge_rejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ACTIVE)));

        assertThatThrownBy(() -> action.execute(command(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 12, 31))))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.SCHEDULE_RUN_RANGE_TOO_LARGE.code()));
    }

    @Test
    void createScheduleRun_withoutPermission_forbidden() {
        doThrow(new AppException(ProjectErrorCatalog.SCHEDULE_ACCESS_DENIED))
                .when(authorization).requireScheduleRecalculate(any());

        assertThatThrownBy(() -> action.execute(command(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 10))))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException ae = (AppException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo(ProjectErrorCatalog.SCHEDULE_ACCESS_DENIED.code());
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }

    private CreateScheduleRunCommand command(LocalDate start, LocalDate end) {
        return new CreateScheduleRunCommand(projectId, start, end, false, true);
    }

    private Project project(ProjectStatus status) {
        Project base = Project.create(workspaceId, UUID.randomUUID(), "P1", "Project", null,
                UUID.randomUUID(), "USD", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        return switch (status) {
            case ACTIVE -> base.activate(UUID.randomUUID());
            case ARCHIVED -> base.activate(UUID.randomUUID()).archive(UUID.randomUUID());
            default -> base;
        };
    }
}
