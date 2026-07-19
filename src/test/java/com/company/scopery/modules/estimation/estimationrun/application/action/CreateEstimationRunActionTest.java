package com.company.scopery.modules.estimation.estimationrun.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.estimation.calculation.EstimationEngineService;
import com.company.scopery.modules.estimation.estimationrun.application.command.CreateEstimationRunCommand;
import com.company.scopery.modules.estimation.estimationrun.application.response.EstimationRunResponse;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CalculationMode;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CurrencyPolicy;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.RateTargetDateStrategy;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.shared.activity.EstimationActivityLogger;
import com.company.scopery.modules.estimation.shared.authorization.EstimationAuthorizationService;
import com.company.scopery.modules.estimation.shared.error.EstimationErrorCatalog;
import com.company.scopery.modules.estimation.shared.support.EstimationPlatformPublisher;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateEstimationRunActionTest {

    @Mock ProjectRepository projects;
    @Mock EstimationRunRepository runs;
    @Mock EstimationAuthorizationService authorization;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock EstimationEngineService engine;
    @Mock EstimationPlatformPublisher publisher;
    @Mock EstimationActivityLogger activityLogger;
    @Mock IamUser iamUser;

    private CreateEstimationRunAction action;
    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateEstimationRunAction(projects, runs, authorization, currentUser,
                engine, publisher, activityLogger);
    }

    @Test
    void archivedProjectRejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ARCHIVED)));

        assertThatThrownBy(() -> action.execute(command("TASK_ESTIMATE_WITH_RATE")))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException ae = (AppException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo(EstimationErrorCatalog.ESTIMATION_RUN_PROJECT_ARCHIVED.code());
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                });
    }

    @Test
    void invalidModeRejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ACTIVE)));

        assertThatThrownBy(() -> action.execute(command("NOT_A_MODE")))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(EstimationErrorCatalog.ESTIMATION_RUN_INVALID_MODE.code()));
    }

    @Test
    void successMarksCompleted() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ACTIVE)));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(UUID.randomUUID());
        when(runs.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(engine.execute(any(), any())).thenAnswer(inv -> {
            EstimationRun run = inv.getArgument(0);
            return run.running().completed("{\"ok\":true}");
        });

        EstimationRunResponse response = action.execute(command("TASK_ESTIMATE_WITH_RATE"));

        assertThat(response.status()).isEqualTo(EstimationRunStatus.COMPLETED.name());
        assertThat(response.calculationMode()).isEqualTo(CalculationMode.TASK_ESTIMATE_WITH_RATE.name());
        assertThat(response.rateTargetDateStrategy())
                .isEqualTo(RateTargetDateStrategy.TASK_DUE_DATE_OR_PROJECT_START.name());
        assertThat(response.currencyPolicy()).isEqualTo(CurrencyPolicy.SINGLE_CURRENCY_REQUIRED.name());
    }

    private CreateEstimationRunCommand command(String mode) {
        return new CreateEstimationRunCommand(projectId, "Initial", null, null, mode, null, null,
                false, false, false, true, true);
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
