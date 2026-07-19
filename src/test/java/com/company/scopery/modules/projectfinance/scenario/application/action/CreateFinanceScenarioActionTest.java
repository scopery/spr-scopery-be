package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummary;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.projectfinance.calculation.FinanceCalculationService;
import com.company.scopery.modules.projectfinance.calculation.FinanceScenarioRecalculator;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.application.command.CreateFinanceScenarioCommand;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.activity.ProjectFinanceActivityLogger;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceErrorCatalog;
import com.company.scopery.modules.projectfinance.shared.support.ProjectFinancePlatformPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateFinanceScenarioActionTest {

    @Mock ProjectRepository projects;
    @Mock EstimationRunRepository estimationRuns;
    @Mock ProjectEstimateSummaryRepository estimateSummaries;
    @Mock ProjectFinanceScenarioRepository scenarios;
    @Mock ProjectFinanceAuthorizationService authorization;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock FinanceScenarioRecalculator recalculator;
    @Mock FinanceCalculationService calculationService;
    @Mock ProjectPhaseFinanceRepository phaseFinance;
    @Mock ProjectFinancePlatformPublisher publisher;
    @Mock ProjectFinanceActivityLogger activityLogger;
    @Mock IamUser iamUser;

    private CreateFinanceScenarioAction action;
    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID estimationRunId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateFinanceScenarioAction(
                projects, estimationRuns, estimateSummaries, scenarios, authorization, currentUser,
                recalculator, calculationService, phaseFinance, publisher, activityLogger);
    }

    @Test
    void archivedProjectRejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ARCHIVED)));

        assertThatThrownBy(() -> action.execute(command()))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException ae = (AppException) ex;
                    assertThat(ae.getErrorCode())
                            .isEqualTo(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_PROJECT_ARCHIVED.code());
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                });
    }

    @Test
    void estimationNotCompletedRejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ACTIVE)));
        when(estimationRuns.findById(estimationRunId)).thenReturn(Optional.of(estimationRun(EstimationRunStatus.RUNNING)));

        assertThatThrownBy(() -> action.execute(command()))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_COMPLETED.code()));
    }

    @Test
    void estimationProjectMismatchRejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ACTIVE)));
        EstimationRun run = estimationRun(EstimationRunStatus.COMPLETED);
        when(estimationRuns.findById(estimationRunId)).thenReturn(Optional.of(
                new EstimationRun(run.id(), UUID.randomUUID(), workspaceId, null, run.name(), null,
                        run.status(), run.calculationMode(), run.rateTargetDateStrategy(), run.currencyPolicy(),
                        null, null, null, null, null, null, null, null, 0, null, null)));

        assertThatThrownBy(() -> action.execute(command()))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_ESTIMATION_PROJECT_MISMATCH.code()));
    }

    @Test
    void createSuccessImportsAndStoresFormulaVersion() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project(ProjectStatus.ACTIVE)));
        when(estimationRuns.findById(estimationRunId))
                .thenReturn(Optional.of(estimationRun(EstimationRunStatus.COMPLETED)));
        when(estimateSummaries.findByEstimationRunId(estimationRunId))
                .thenReturn(Optional.of(ProjectEstimateSummary.create(
                        estimationRunId, projectId, 1, 1, 0, 0, 0, 0,
                        new BigDecimal("10"), new BigDecimal("100"), BigDecimal.ZERO,
                        null, null, "USD", 0)));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(UUID.randomUUID());
        when(scenarios.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FinanceScenarioResponse response = action.execute(command());

        assertThat(response.status()).isEqualTo(FinanceScenarioStatus.DRAFT.name());
        assertThat(response.formulaVersion()).isEqualTo("FINANCE_V1");
        assertThat(response.estimationRunId()).isEqualTo(estimationRunId);
        verify(recalculator).recalculate(any(ProjectFinanceScenario.class), eq(true));
        verify(publisher).enqueueScenario(any(), eq("PROJECT_FINANCE_SCENARIO_CREATED"));
    }

    private CreateFinanceScenarioCommand command() {
        return new CreateFinanceScenarioCommand(
                projectId, "Initial", null, null, estimationRunId, "USD",
                new BigDecimal("1000"), "COST_PROPORTION",
                "PERCENT_OF_DIRECT_COST", new BigDecimal("10"), null,
                "PERCENT_OF_LABOR_COST", new BigDecimal("15"), null,
                null, null, false);
    }

    private Project project(ProjectStatus status) {
        Project base = new Project(
                projectId, workspaceId, UUID.randomUUID(), "P1", "Project", null,
                UUID.randomUUID(), "USD", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                ProjectStatus.DRAFT,
                null, null, null, null,
                null, null, null,
                null, null, null,
                null, null, null,
                0, null, null);
        return switch (status) {
            case ACTIVE -> base.activate(UUID.randomUUID());
            case ARCHIVED -> base.activate(UUID.randomUUID()).archive(UUID.randomUUID());
            default -> base;
        };
    }

    private EstimationRun estimationRun(EstimationRunStatus status) {
        EstimationRun run = new EstimationRun(
                estimationRunId, projectId, workspaceId, null, "Run", null,
                EstimationRunStatus.PENDING,
                com.company.scopery.modules.estimation.estimationrun.domain.enums.CalculationMode.TASK_ESTIMATE_WITH_RATE,
                com.company.scopery.modules.estimation.estimationrun.domain.enums.RateTargetDateStrategy.TASK_DUE_DATE_OR_PROJECT_START,
                com.company.scopery.modules.estimation.estimationrun.domain.enums.CurrencyPolicy.SINGLE_CURRENCY_REQUIRED,
                null, null, null, null, null, null, UUID.randomUUID(), "trace", 0, null, null);
        return switch (status) {
            case COMPLETED -> run.running().completed("{}");
            case RUNNING -> run.running();
            default -> run;
        };
    }
}
