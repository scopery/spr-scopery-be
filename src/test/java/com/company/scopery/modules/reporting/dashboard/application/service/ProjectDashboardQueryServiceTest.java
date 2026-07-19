package com.company.scopery.modules.reporting.dashboard.application.service;

import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollupRepository;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshotRepository;
import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollupRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssueRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpactRepository;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrderRepository;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.reporting.dashboard.application.response.CapacityReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectDashboardResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectHealthResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ScheduleRiskReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.TaskRiskReportResponse;
import com.company.scopery.modules.reporting.dashboard.domain.enums.HealthStatus;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.error.ReportingErrorCatalog;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.common.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectDashboardQueryServiceTest {

    @Mock ProjectRepository projects;
    @Mock TaskRepository tasks;
    @Mock ProjectBaselineRepository baselines;
    @Mock ChangeRequestRepository changeRequests;
    @Mock ChangeImpactRepository changeImpacts;
    @Mock ChangeOrderRepository changeOrders;
    @Mock AiPlanningSuggestionRepository suggestions;
    @Mock ScheduleRunRepository scheduleRuns;
    @Mock TaskScheduleRepository taskSchedules;
    @Mock SchedulingIssueRepository schedulingIssues;
    @Mock ProjectResourceAllocationRepository allocations;
    @Mock EstimationRunRepository estimationRuns;
    @Mock ProjectEstimateSummaryRepository estimateSummaries;
    @Mock PhaseEstimateRollupRepository phaseRollups;
    @Mock WbsEstimateRollupRepository wbsRollups;
    @Mock TaskEstimateSnapshotRepository taskSnapshots;
    @Mock ProjectFinanceScenarioRepository financeScenarios;
    @Mock ProjectFinanceSummaryRepository financeSummaries;
    @Mock ProjectPhaseFinanceRepository phaseFinance;
    @Mock QuoteVersionRepository quoteVersions;
    @Mock QuoteSummaryRepository quoteSummaries;
    @Mock ReportingAuthorizationService authorization;

    private ProjectDashboardQueryService service;
    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ProjectDashboardQueryService(
                projects, tasks, baselines, changeRequests, changeImpacts, changeOrders, suggestions,
                scheduleRuns, taskSchedules, schedulingIssues, allocations, estimationRuns, estimateSummaries,
                phaseRollups, wbsRollups, taskSnapshots, financeScenarios, financeSummaries, phaseFinance,
                quoteVersions, quoteSummaries, authorization, new ObjectMapper());
    }

    @Test
    void taskRiskCountsOverdueAndAtRisk() {
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(
                task(TaskStatus.TODO, LocalDate.now().minusDays(1), true),
                task(TaskStatus.BLOCKED, LocalDate.now().plusDays(1), true),
                task(TaskStatus.DONE, LocalDate.now().minusDays(10), true),
                task(TaskStatus.ARCHIVED, LocalDate.now().minusDays(1), true)
        ));

        TaskRiskReportResponse result = service.taskRisk(projectId);

        assertThat(result.totalTasks()).isEqualTo(3L);
        assertThat(result.overdueTasks()).isEqualTo(1L);
        assertThat(result.blockedTasks()).isEqualTo(1L);
        assertThat(result.atRiskTasks()).isEqualTo(2L);
    }

    @Test
    void projectHealthGreenWhenNoIssues() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(
                task(TaskStatus.TODO, LocalDate.now().plusDays(5), true)
        ));

        ProjectHealthResponse health = service.health(projectId);

        assertThat(health.status()).isEqualTo(HealthStatus.GREEN.name());
        assertThat(health.formulaVersion()).isEqualTo("v1");
    }

    @Test
    void projectHealthYellowWhenOverdue() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(
                task(TaskStatus.TODO, LocalDate.now().minusDays(1), true)
        ));

        ProjectHealthResponse health = service.health(projectId);

        assertThat(health.status()).isEqualTo(HealthStatus.YELLOW.name());
    }

    @Test
    void projectHealthRedWhenBlocked() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(
                task(TaskStatus.BLOCKED, LocalDate.now().plusDays(1), true)
        ));

        ProjectHealthResponse health = service.health(projectId);

        assertThat(health.status()).isEqualTo(HealthStatus.RED.name());
    }

    @Test
    void projectHealthUnknownWhenNoTasks() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of());

        ProjectHealthResponse health = service.health(projectId);

        assertThat(health.status()).isEqualTo(HealthStatus.UNKNOWN.name());
    }

    @Test
    void dashboardMasksFinanceWithoutPermission() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of());
        when(baselines.findCurrentByProjectId(projectId)).thenReturn(Optional.empty());
        when(changeRequests.findByProjectId(projectId)).thenReturn(List.of());
        when(suggestions.findByProjectId(projectId)).thenReturn(List.of());
        when(authorization.canViewFinance(projectId)).thenReturn(false);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        ProjectDashboardResponse dashboard = service.dashboard(projectId);

        assertThat(dashboard.finance().detailsRedacted()).isTrue();
    }

    @Test
    void financeReportRequiresFinancePermission() {
        when(authorization.canViewFinance(projectId)).thenReturn(false);

        assertThatThrownBy(() -> service.finance(projectId))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ReportingErrorCatalog.REPORT_FINANCE_ACCESS_DENIED.code()));
    }

    @Test
    void quoteReportRequiresQuotePermission() {
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        assertThatThrownBy(() -> service.quote(projectId))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ReportingErrorCatalog.REPORT_QUOTE_ACCESS_DENIED.code()));
    }

    @Test
    void capacityMasksPrivateLeaveDetails() {
        when(allocations.findActiveByProjectId(projectId)).thenReturn(List.of());

        CapacityReportResponse capacity = service.capacity(projectId);

        assertThat(capacity.privateLeaveDetailsMasked()).isTrue();
        assertThat(capacity.allocatedUsers()).isEqualTo(0L);
    }

    @Test
    void scheduleRiskReturnsUnknownWhenNoCurrentRun() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));

        ScheduleRiskReportResponse report = service.scheduleRisk(projectId);

        assertThat(report.sourceAvailable()).isFalse();
        assertThat(report.status()).isEqualTo(HealthStatus.UNKNOWN.name());
    }

    private Project project() {
        Project base = Project.create(workspaceId, UUID.randomUUID(), "P1", "Project", null,
                UUID.randomUUID(), "USD", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        return base.activate(UUID.randomUUID());
    }

    private Task task(TaskStatus status, LocalDate dueDate, boolean withAssignee) {
        Task created = Task.create(
                projectId, null, null, "T-" + UUID.randomUUID().toString().substring(0, 8),
                "Task", null, withAssignee ? UUID.randomUUID() : null, null, null,
                BigDecimal.ONE, dueDate, dueDate, TaskPriority.MEDIUM);
        return switch (status) {
            case TODO -> created;
            case BLOCKED -> created.block();
            case DONE -> created.start(UUID.randomUUID()).complete(UUID.randomUUID());
            case ARCHIVED -> created.archive(UUID.randomUUID());
            default -> created;
        };
    }
}
