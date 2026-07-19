package com.company.scopery.modules.estimation.calculation;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.estimation.costrole.CostRoleResolutionService;
import com.company.scopery.modules.estimation.costrole.ResolvedCostRole;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CalculationMode;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CurrencyPolicy;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.RateTargetDateStrategy;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollupRepository;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummary;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.estimation.shared.error.EstimationErrorCatalog;
import com.company.scopery.modules.estimation.tasksnapshot.domain.enums.TaskSnapshotStatus;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshot;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshotRepository;
import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollup;
import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollupRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.ratecard.resolution.application.service.RateResolutionService;
import com.company.scopery.modules.ratecard.resolution.domain.RateSnapshot;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstimationEngineServiceTest {

    @Mock EstimationRunRepository runs;
    @Mock TaskEstimateSnapshotRepository snapshots;
    @Mock WbsEstimateRollupRepository wbsRollups;
    @Mock PhaseEstimateRollupRepository phaseRollups;
    @Mock ProjectEstimateSummaryRepository summaries;
    @Mock TaskRepository tasks;
    @Mock WbsNodeRepository wbsNodes;
    @Mock ProjectRepository projects;
    @Mock CostRoleResolutionService costRoleResolutionService;
    @Mock RateResolutionService rateResolutionService;

    private EstimationEngineService engine;
    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID orgId = UUID.randomUUID();
    private final UUID phaseId = UUID.randomUUID();
    private final UUID parentWbsId = UUID.randomUUID();
    private final UUID childWbsId = UUID.randomUUID();
    private final UUID roleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        engine = new EstimationEngineService(runs, snapshots, wbsRollups, phaseRollups, summaries,
                tasks, wbsNodes, projects, costRoleResolutionService, rateResolutionService);
        lenient().when(runs.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(snapshots.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(wbsRollups.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(phaseRollups.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(summaries.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void calculatesLaborCostAndBillingPreview() {
        Project project = activeProject();
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        Task task = task(UUID.randomUUID(), childWbsId, phaseId, new BigDecimal("10"), TaskStatus.TODO, "DEV");
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(wbsNodes.findAllByProjectId(projectId)).thenReturn(wbsTree());
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(new ResolvedCostRole(roleId, "DEV")));
        when(rateResolutionService.resolve(any())).thenReturn(rateSnapshot("USD", "100.0000", "150.0000"));

        EstimationRun result = engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        assertThat(result.status()).isEqualTo(EstimationRunStatus.COMPLETED);
        ArgumentCaptor<List<TaskEstimateSnapshot>> cap = ArgumentCaptor.forClass(List.class);
        verify(snapshots).saveAll(cap.capture());
        TaskEstimateSnapshot snap = cap.getValue().getFirst();
        assertThat(snap.status()).isEqualTo(TaskSnapshotStatus.CALCULATED);
        assertThat(snap.estimatedLaborCost()).isEqualByComparingTo("1000.0000");
        assertThat(snap.estimatedBillingPreview()).isEqualByComparingTo("1500.0000");
    }

    @Test
    void roleUnresolvedDoesNotFailRun() {
        stubProjectAndEmptyWbs();
        Task task = task(UUID.randomUUID(), null, phaseId, new BigDecimal("8"), TaskStatus.TODO, null);
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any())).thenReturn(Optional.empty());

        EstimationRun result = engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        assertThat(result.status()).isEqualTo(EstimationRunStatus.COMPLETED);
        ArgumentCaptor<List<TaskEstimateSnapshot>> cap = ArgumentCaptor.forClass(List.class);
        verify(snapshots).saveAll(cap.capture());
        assertThat(cap.getValue().getFirst().status()).isEqualTo(TaskSnapshotStatus.ROLE_UNRESOLVED);
    }

    @Test
    void rateUnresolvedDoesNotFailRun() {
        stubProjectAndEmptyWbs();
        Task task = task(UUID.randomUUID(), null, phaseId, new BigDecimal("8"), TaskStatus.TODO, "DEV");
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(new ResolvedCostRole(roleId, "DEV")));
        when(rateResolutionService.resolve(any())).thenThrow(RateCardExceptions.noApplicableCard());

        EstimationRun result = engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        assertThat(result.status()).isEqualTo(EstimationRunStatus.COMPLETED);
        ArgumentCaptor<List<TaskEstimateSnapshot>> cap = ArgumentCaptor.forClass(List.class);
        verify(snapshots).saveAll(cap.capture());
        assertThat(cap.getValue().getFirst().status()).isEqualTo(TaskSnapshotStatus.RATE_UNRESOLVED);
    }

    @Test
    void wbsParentIncludesDescendantsWithoutDoubleCount() {
        Project project = activeProject();
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        Task childTask = task(UUID.randomUUID(), childWbsId, phaseId, new BigDecimal("5"), TaskStatus.TODO, "DEV");
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(childTask));
        when(wbsNodes.findAllByProjectId(projectId)).thenReturn(wbsTree());
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(new ResolvedCostRole(roleId, "DEV")));
        when(rateResolutionService.resolve(any())).thenReturn(rateSnapshot("USD", "10.0000", null));

        engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        ArgumentCaptor<List<WbsEstimateRollup>> cap = ArgumentCaptor.forClass(List.class);
        verify(wbsRollups).saveAll(cap.capture());
        WbsEstimateRollup parent = cap.getValue().stream().filter(r -> r.wbsNodeId().equals(parentWbsId)).findFirst().orElseThrow();
        WbsEstimateRollup child = cap.getValue().stream().filter(r -> r.wbsNodeId().equals(childWbsId)).findFirst().orElseThrow();
        assertThat(parent.totalEstimateHours()).isEqualByComparingTo("5.00");
        assertThat(child.totalEstimateHours()).isEqualByComparingTo("5.00");
        assertThat(parent.totalLaborCost()).isEqualByComparingTo("50.0000");
    }

    @Test
    void taskWithoutWbsIncludedInProjectOnly() {
        stubProjectAndEmptyWbs();
        Task task = task(UUID.randomUUID(), null, phaseId, new BigDecimal("4"), TaskStatus.TODO, "DEV");
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(new ResolvedCostRole(roleId, "DEV")));
        when(rateResolutionService.resolve(any())).thenReturn(rateSnapshot("USD", "20.0000", null));

        engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        ArgumentCaptor<List<WbsEstimateRollup>> wbsCap = ArgumentCaptor.forClass(List.class);
        verify(wbsRollups).saveAll(wbsCap.capture());
        assertThat(wbsCap.getValue()).isEmpty();

        ArgumentCaptor<ProjectEstimateSummary> sumCap = ArgumentCaptor.forClass(ProjectEstimateSummary.class);
        verify(summaries).save(sumCap.capture());
        assertThat(sumCap.getValue().totalEstimateHours()).isEqualByComparingTo("4.00");
        assertThat(sumCap.getValue().totalLaborCost()).isEqualByComparingTo("80.0000");
    }

    @Test
    void phaseRollupAndSummaryAverages() {
        stubProjectAndEmptyWbs();
        Task t1 = task(UUID.randomUUID(), null, phaseId, new BigDecimal("10"), TaskStatus.TODO, "DEV");
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(t1));
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(new ResolvedCostRole(roleId, "DEV")));
        when(rateResolutionService.resolve(any())).thenReturn(rateSnapshot("USD", "50.0000", "80.0000"));

        engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        ArgumentCaptor<List<PhaseEstimateRollup>> phaseCap = ArgumentCaptor.forClass(List.class);
        verify(phaseRollups).saveAll(phaseCap.capture());
        assertThat(phaseCap.getValue()).hasSize(1);
        assertThat(phaseCap.getValue().getFirst().totalLaborCost()).isEqualByComparingTo("500.0000");

        ArgumentCaptor<ProjectEstimateSummary> sumCap = ArgumentCaptor.forClass(ProjectEstimateSummary.class);
        verify(summaries).save(sumCap.capture());
        assertThat(sumCap.getValue().averageCostRate()).isEqualByComparingTo("50.0000");
        assertThat(sumCap.getValue().averageBillingRate()).isEqualByComparingTo("80.0000");
    }

    @Test
    void mixedCurrencyFailsRun() {
        Project project = activeProject();
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        Task t1 = task(UUID.randomUUID(), null, phaseId, new BigDecimal("2"), TaskStatus.TODO, "DEV");
        Task t2 = task(UUID.randomUUID(), null, phaseId, new BigDecimal("3"), TaskStatus.TODO, "DEV");
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(t1, t2));
        when(wbsNodes.findAllByProjectId(projectId)).thenReturn(List.of());
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(new ResolvedCostRole(roleId, "DEV")));
        when(rateResolutionService.resolve(any()))
                .thenReturn(rateSnapshot("USD", "10.0000", null))
                .thenReturn(rateSnapshot("VND", "10.0000", null));

        EstimationRun result = engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        assertThat(result.status()).isEqualTo(EstimationRunStatus.FAILED);
        assertThat(result.errorCode()).isEqualTo(EstimationErrorCatalog.ESTIMATION_MIXED_CURRENCY_NOT_ALLOWED.code());
    }

    @Test
    void snapshotValuesIndependentOfLaterRateChanges() {
        stubProjectAndEmptyWbs();
        Task task = task(UUID.randomUUID(), null, phaseId, new BigDecimal("1"), TaskStatus.TODO, "DEV");
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(costRoleResolutionService.resolve(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(new ResolvedCostRole(roleId, "DEV")));
        when(rateResolutionService.resolve(any())).thenReturn(rateSnapshot("USD", "12.3456", null));

        engine.execute(pendingRun(CalculationMode.TASK_ESTIMATE_WITH_RATE,
                CurrencyPolicy.SINGLE_CURRENCY_REQUIRED), EstimationEngineOptions.defaults());

        ArgumentCaptor<List<TaskEstimateSnapshot>> cap = ArgumentCaptor.forClass(List.class);
        verify(snapshots).saveAll(cap.capture());
        TaskEstimateSnapshot snap = cap.getValue().getFirst();
        assertThat(snap.adjustedCostRate()).isEqualByComparingTo("12.3456");
        assertThat(snap.estimatedLaborCost()).isEqualByComparingTo("12.3456");
        // conceptual immutability: no salary fields on snapshot record
        assertThat(TaskEstimateSnapshot.class.getRecordComponents())
                .extracting(rc -> rc.getName())
                .doesNotContain("salary", "payroll", "baseSalary");
    }

    @Test
    void rateTargetDateStrategies() {
        Project project = activeProject();
        Task withDue = task(UUID.randomUUID(), null, phaseId, new BigDecimal("1"), TaskStatus.TODO, "DEV");
        // due date set in helper to 2026-09-15; planned start on project 2026-01-01
        assertThat(engine.resolveRateTargetDate(RateTargetDateStrategy.TASK_DUE_DATE_OR_PROJECT_START, project, withDue))
                .isEqualTo(LocalDate.of(2026, 9, 15));
        Task noDue = Task.create(projectId, phaseId, null, "T2", "No due", null, null, null, null,
                new BigDecimal("1"), null, null, TaskPriority.MEDIUM);
        assertThat(engine.resolveRateTargetDate(RateTargetDateStrategy.TASK_DUE_DATE_OR_PROJECT_START, project, noDue))
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(engine.resolveRateTargetDate(RateTargetDateStrategy.PROJECT_PLANNED_START, project, withDue))
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(engine.resolveRateTargetDate(RateTargetDateStrategy.TASK_DUE_DATE, project, withDue))
                .isEqualTo(LocalDate.of(2026, 9, 15));
    }

    private void stubProjectAndEmptyWbs() {
        when(projects.findById(projectId)).thenReturn(Optional.of(activeProject()));
        when(wbsNodes.findAllByProjectId(projectId)).thenReturn(List.of());
    }

    private EstimationRun pendingRun(CalculationMode mode, CurrencyPolicy policy) {
        return EstimationRun.create(projectId, workspaceId, null, "Run", null, mode,
                RateTargetDateStrategy.TASK_DUE_DATE_OR_PROJECT_START, policy, "{}", null, "trace");
    }

    private Project activeProject() {
        return Project.create(workspaceId, orgId, "P1", "Project", null, UUID.randomUUID(), "USD",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)).activate(UUID.randomUUID());
    }

    private Task task(UUID id, UUID wbsId, UUID phase, BigDecimal hours, TaskStatus status, String role) {
        Task base = Task.create(projectId, phase, wbsId, "T-" + id.toString().substring(0, 4), "Task", null,
                UUID.randomUUID(), role, role, hours, null, LocalDate.of(2026, 9, 15), TaskPriority.MEDIUM);
        return switch (status) {
            case TODO -> base;
            case DONE -> base.complete(UUID.randomUUID());
            case CANCELLED -> base.cancel(UUID.randomUUID());
            case ARCHIVED -> base.archive(UUID.randomUUID());
            default -> base;
        };
    }

    private List<WbsNode> wbsTree() {
        WbsNode parent = WbsNode.create(projectId, phaseId, null, "1", "Parent", null,
                WbsNodeType.TASK_GROUP, 0, "/1", 1);
        parent = new WbsNode(parentWbsId, parent.projectId(), parent.projectPhaseId(), parent.parentId(),
                parent.code(), parent.title(), parent.description(), parent.nodeType(), parent.level(),
                parent.path(), parent.sortOrder(), WbsNodeStatus.ACTIVE, 0, null, null);
        WbsNode child = WbsNode.create(projectId, phaseId, parentWbsId, "1.1", "Child", null,
                WbsNodeType.WORK_PACKAGE, 1, "/1/1.1", 1);
        child = new WbsNode(childWbsId, child.projectId(), child.projectPhaseId(), child.parentId(),
                child.code(), child.title(), child.description(), child.nodeType(), child.level(),
                child.path(), child.sortOrder(), WbsNodeStatus.ACTIVE, 0, null, null);
        return List.of(parent, child);
    }

    private RateSnapshot rateSnapshot(String currency, String cost, String billing) {
        return new RateSnapshot(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), roleId, "DEV",
                new BigDecimal(cost), new BigDecimal(cost),
                billing == null ? null : new BigDecimal(billing),
                billing == null ? null : new BigDecimal(billing),
                currency, UUID.randomUUID(), BigDecimal.ZERO, BigDecimal.ZERO,
                LocalDate.of(2026, 9, 15), Instant.parse("2026-07-12T00:00:00Z"));
    }
}
