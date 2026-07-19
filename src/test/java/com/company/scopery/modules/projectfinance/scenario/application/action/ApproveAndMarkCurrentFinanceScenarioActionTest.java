package com.company.scopery.modules.projectfinance.scenario.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.projectfinance.calculation.FinanceCalculationService;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.ContingencyMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.OverheadMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.shared.activity.ProjectFinanceActivityLogger;
import com.company.scopery.modules.projectfinance.shared.authorization.ProjectFinanceAuthorizationService;
import com.company.scopery.modules.projectfinance.shared.error.ProjectFinanceErrorCatalog;
import com.company.scopery.modules.projectfinance.shared.support.ProjectFinancePlatformPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApproveAndMarkCurrentFinanceScenarioActionTest {

    @Mock ProjectRepository projects;
    @Mock ProjectFinanceScenarioRepository scenarios;
    @Mock ProjectPhaseFinanceRepository phaseFinance;
    @Mock ProjectEstimateSummaryRepository estimateSummaries;
    @Mock FinanceCalculationService calculationService;
    @Mock ProjectFinanceAuthorizationService authorization;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock ProjectFinancePlatformPublisher publisher;
    @Mock ProjectFinanceActivityLogger activityLogger;
    @Mock ApplicationEventPublisher events;
    @Mock IamUser iamUser;

    private ApproveFinanceScenarioAction approve;
    private MarkCurrentFinanceScenarioAction markCurrent;
    private final UUID projectId = UUID.randomUUID();
    private final UUID scenarioId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        approve = new ApproveFinanceScenarioAction(
                projects, scenarios, phaseFinance, estimateSummaries, calculationService,
                authorization, currentUser, publisher, activityLogger, events);
        markCurrent = new MarkCurrentFinanceScenarioAction(
                projects, scenarios, phaseFinance, estimateSummaries, calculationService,
                authorization, currentUser, publisher, activityLogger, events);
    }

    @Test
    void approveDraftSuccess() {
        Project project = activeProject();
        ProjectFinanceScenario scenario = draftScenario();
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        when(scenarios.findByIdAndProjectId(scenarioId, projectId)).thenReturn(Optional.of(scenario));
        when(estimateSummaries.findByEstimationRunId(any())).thenReturn(Optional.empty());
        when(phaseFinance.findByScenarioId(scenarioId)).thenReturn(List.of());
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(UUID.randomUUID());
        when(scenarios.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FinanceScenarioResponse response = approve.execute(projectId, scenarioId);

        assertThat(response.status()).isEqualTo(FinanceScenarioStatus.APPROVED.name());
        verify(publisher).enqueueScenario(any(), org.mockito.ArgumentMatchers.eq("PROJECT_FINANCE_SCENARIO_APPROVED"));
    }

    @Test
    void updateApprovedRejectedViaNotDraft() {
        ProjectFinanceScenario approved = draftScenario().approve(UUID.randomUUID());
        when(projects.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(scenarios.findByIdAndProjectId(scenarioId, projectId)).thenReturn(Optional.of(approved));

        assertThatThrownBy(() -> approve.execute(projectId, scenarioId))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_ALREADY_APPROVED.code()));
    }

    @Test
    void markCurrentClearsPreviousCurrent() {
        Project project = activeProject();
        ProjectFinanceScenario approved = draftScenario().approve(UUID.randomUUID());
        ProjectFinanceScenario previous = draftScenario().approve(UUID.randomUUID()).withCurrentFlag(true);
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        when(scenarios.findByIdAndProjectId(scenarioId, projectId)).thenReturn(Optional.of(approved));
        when(scenarios.findCurrentFlagged(projectId)).thenReturn(List.of(previous));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(UUID.randomUUID());
        when(scenarios.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(projects.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FinanceScenarioResponse response = markCurrent.execute(projectId, scenarioId);

        assertThat(response.currentFlag()).isTrue();
        verify(projects).save(any(Project.class));
        verify(publisher).enqueueScenario(any(), org.mockito.ArgumentMatchers.eq("PROJECT_FINANCE_SCENARIO_MARKED_CURRENT"));
    }

    private Project activeProject() {
        return Project.create(UUID.randomUUID(), UUID.randomUUID(), "P1", "Project", null,
                UUID.randomUUID(), "USD", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
                .activate(UUID.randomUUID());
    }

    private ProjectFinanceScenario draftScenario() {
        return new ProjectFinanceScenario(
                scenarioId, projectId, UUID.randomUUID(), UUID.randomUUID(), null, "S1", null, 1,
                FinanceScenarioStatus.DRAFT, "USD", new BigDecimal("1000"),
                RevenueSplitMethod.COST_PROPORTION, ContingencyMethod.FIXED_AMOUNT,
                null, BigDecimal.ZERO, OverheadMethod.FIXED_AMOUNT, null, BigDecimal.ZERO,
                null, null, "FINANCE_V1", false, null, null, null, null,
                UUID.randomUUID(), "trace", 0, null, null);
    }
}
