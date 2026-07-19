package com.company.scopery.modules.estimation.calculation;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.estimation.costrole.CostRoleResolutionService;
import com.company.scopery.modules.estimation.costrole.ResolvedCostRole;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CalculationMode;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CurrencyPolicy;
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
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.ratecard.resolution.application.query.ResolveRateQuery;
import com.company.scopery.modules.ratecard.resolution.application.service.RateResolutionService;
import com.company.scopery.modules.ratecard.resolution.domain.RateSnapshot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstimationEngineService {

    private static final int MONEY_SCALE = 4;
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final EstimationRunRepository runs;
    private final TaskEstimateSnapshotRepository snapshots;
    private final WbsEstimateRollupRepository wbsRollups;
    private final PhaseEstimateRollupRepository phaseRollups;
    private final ProjectEstimateSummaryRepository summaries;
    private final TaskRepository tasks;
    private final WbsNodeRepository wbsNodes;
    private final ProjectRepository projects;
    private final CostRoleResolutionService costRoleResolutionService;
    private final RateResolutionService rateResolutionService;

    public EstimationEngineService(EstimationRunRepository runs,
                                   TaskEstimateSnapshotRepository snapshots,
                                   WbsEstimateRollupRepository wbsRollups,
                                   PhaseEstimateRollupRepository phaseRollups,
                                   ProjectEstimateSummaryRepository summaries,
                                   TaskRepository tasks,
                                   WbsNodeRepository wbsNodes,
                                   ProjectRepository projects,
                                   CostRoleResolutionService costRoleResolutionService,
                                   RateResolutionService rateResolutionService) {
        this.runs = runs;
        this.snapshots = snapshots;
        this.wbsRollups = wbsRollups;
        this.phaseRollups = phaseRollups;
        this.summaries = summaries;
        this.tasks = tasks;
        this.wbsNodes = wbsNodes;
        this.projects = projects;
        this.costRoleResolutionService = costRoleResolutionService;
        this.rateResolutionService = rateResolutionService;
    }

    @Transactional
    public EstimationRun execute(EstimationRun run, EstimationEngineOptions options) {
        EstimationRun current = runs.save(run.running());
        try {
            Project project = projects.findById(current.projectId())
                    .orElseThrow(() -> new IllegalStateException("Project missing for estimation run"));
            List<Task> projectTasks = tasks.findAllByProjectId(current.projectId());
            List<WbsNode> nodes = wbsNodes.findAllByProjectId(current.projectId());

            List<TaskEstimateSnapshot> taskSnapshots = new ArrayList<>();
            for (Task task : projectTasks) {
                taskSnapshots.add(buildTaskSnapshot(current, project, task, options));
            }
            snapshots.saveAll(taskSnapshots);

            if (current.currencyPolicy() == CurrencyPolicy.SINGLE_CURRENCY_REQUIRED) {
                Set<String> currencies = taskSnapshots.stream()
                        .filter(s -> s.status() == TaskSnapshotStatus.CALCULATED)
                        .map(TaskEstimateSnapshot::currencyCode)
                        .filter(c -> c != null && !c.isBlank())
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                if (currencies.size() > 1) {
                    throw new AppException(EstimationErrorCatalog.ESTIMATION_MIXED_CURRENCY_NOT_ALLOWED);
                }
            }

            List<WbsEstimateRollup> wbs = buildWbsRollups(current, nodes, taskSnapshots);
            wbsRollups.saveAll(wbs);

            List<PhaseEstimateRollup> phases = buildPhaseRollups(current, taskSnapshots);
            phaseRollups.saveAll(phases);

            ProjectEstimateSummary summary = buildSummary(current, taskSnapshots);
            summaries.save(summary);

            String resultJson = "{\"totalEstimateHours\":" + summary.totalEstimateHours()
                    + ",\"totalLaborCost\":" + summary.totalLaborCost()
                    + ",\"includedTaskCount\":" + summary.includedTaskCount()
                    + ",\"warningCount\":" + summary.warningCount() + "}";

            EstimationRun completed = runs.save(current.completed(resultJson));
            if (options.markAsCurrent()) {
                projects.findById(completed.projectId())
                        .ifPresent(p -> projects.save(p.withCurrentEstimationRunId(completed.id())));
            }
            return completed;
        } catch (AppException ex) {
            return runs.save(current.failed(ex.getErrorCode(), ex.getMessage()));
        } catch (RuntimeException ex) {
            return runs.save(current.failed(
                    EstimationErrorCatalog.ESTIMATION_RUN_FAILED.code(),
                    ex.getMessage() != null ? ex.getMessage() : "Estimation failed"));
        }
    }

    TaskEstimateSnapshot buildTaskSnapshot(EstimationRun run,
                                           Project project,
                                           Task task,
                                           EstimationEngineOptions options) {
        BigDecimal hours = task.estimateHours() != null ? task.estimateHours() : ZERO;
        LocalDate rateDate = resolveRateTargetDate(run.rateTargetDateStrategy(), project, task);

        if (isExcluded(task, options)) {
            return TaskEstimateSnapshot.create(
                    run.id(), run.projectId(), task.projectPhaseId(), task.wbsNodeId(),
                    task.id(), task.code(), task.title(), task.inChargeUserId(), null,
                    null, null, hours, rateDate,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null,
                    null, null, TaskSnapshotStatus.EXCLUDED, null, null);
        }

        if (task.estimateHours() == null || task.estimateHours().compareTo(BigDecimal.ZERO) <= 0) {
            return TaskEstimateSnapshot.create(
                    run.id(), run.projectId(), task.projectPhaseId(), task.wbsNodeId(),
                    task.id(), task.code(), task.title(), task.inChargeUserId(), null,
                    null, null, hours, rateDate,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null,
                    null, null, TaskSnapshotStatus.TASK_UNESTIMATED,
                    EstimationErrorCatalog.ESTIMATION_TASK_INVALID_ESTIMATE.code(),
                    "Task has no positive estimate hours");
        }

        if (run.calculationMode() == CalculationMode.TASK_ESTIMATE_ONLY) {
            return TaskEstimateSnapshot.create(
                    run.id(), run.projectId(), task.projectPhaseId(), task.wbsNodeId(),
                    task.id(), task.code(), task.title(), task.inChargeUserId(), null,
                    null, null, hours, rateDate,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null,
                    null, null, TaskSnapshotStatus.CALCULATED, null, null);
        }

        Optional<ResolvedCostRole> role = costRoleResolutionService.resolve(
                project.workspaceId(), project.organizationId(),
                task.plannedRoleCode(), task.inChargeUserId(), rateDate);
        if (role.isEmpty()) {
            return TaskEstimateSnapshot.create(
                    run.id(), run.projectId(), task.projectPhaseId(), task.wbsNodeId(),
                    task.id(), task.code(), task.title(), task.inChargeUserId(), null,
                    null, null, hours, rateDate,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null,
                    null, null, TaskSnapshotStatus.ROLE_UNRESOLVED,
                    EstimationErrorCatalog.ESTIMATION_TASK_COST_ROLE_NOT_RESOLVED.code(),
                    "Cost role could not be resolved");
        }

        try {
            RateSnapshot rate = rateResolutionService.resolve(new ResolveRateQuery(
                    project.workspaceId(), project.organizationId(), project.id(),
                    role.get().costRoleId(), role.get().costRoleCode(),
                    rateDate, project.defaultCurrency(), "COST"));
            BigDecimal labor = hours.multiply(rate.adjustedCostRate()).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            BigDecimal billing = null;
            if (options.useBillingPreview() && rate.adjustedBillingRate() != null) {
                billing = hours.multiply(rate.adjustedBillingRate()).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            }
            return TaskEstimateSnapshot.create(
                    run.id(), run.projectId(), task.projectPhaseId(), task.wbsNodeId(),
                    task.id(), task.code(), task.title(), task.inChargeUserId(), null,
                    role.get().costRoleId(), role.get().costRoleCode(), hours, rateDate,
                    rate.rateCardId(), rate.rateCardVersionId(), rate.rateCardLineId(),
                    rate.baseCostRate(), rate.adjustedCostRate(),
                    rate.baseBillingRate(), rate.adjustedBillingRate(),
                    rate.currencyCode(), rate.inflationPolicyId(), rate.inflationPercent(),
                    rate.yearsForward(), rate.resolvedAt(),
                    labor, billing, TaskSnapshotStatus.CALCULATED, null, null);
        } catch (AppException ex) {
            return TaskEstimateSnapshot.create(
                    run.id(), run.projectId(), task.projectPhaseId(), task.wbsNodeId(),
                    task.id(), task.code(), task.title(), task.inChargeUserId(), null,
                    role.get().costRoleId(), role.get().costRoleCode(), hours, rateDate,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null,
                    null, null, TaskSnapshotStatus.RATE_UNRESOLVED,
                    EstimationErrorCatalog.ESTIMATION_TASK_RATE_NOT_RESOLVED.code(),
                    ex.getMessage());
        }
    }

    public LocalDate resolveRateTargetDate(RateTargetDateStrategy strategy, Project project, Task task) {
        LocalDate today = LocalDate.now();
        return switch (strategy) {
            case PROJECT_PLANNED_START -> project.plannedStartDate() != null ? project.plannedStartDate() : today;
            case TASK_DUE_DATE -> task.dueDate() != null ? task.dueDate() : today;
            case TASK_SCHEDULED_START, TASK_SCHEDULED_FINISH ->
                    task.plannedStartDate() != null ? task.plannedStartDate()
                            : (task.dueDate() != null ? task.dueDate() : today);
            case RUN_DATE, CUSTOM_DATE -> today;
            case TASK_DUE_DATE_OR_PROJECT_START -> {
                if (task.dueDate() != null) {
                    yield task.dueDate();
                }
                if (project.plannedStartDate() != null) {
                    yield project.plannedStartDate();
                }
                yield today;
            }
        };
    }

    private boolean isExcluded(Task task, EstimationEngineOptions options) {
        return switch (task.status()) {
            case TODO, IN_PROGRESS, BLOCKED -> false;
            case DONE -> !options.includeCompletedTasks();
            case CANCELLED -> !options.includeCancelledTasks();
            case ARCHIVED -> !options.includeArchivedTasks();
        };
    }

    private List<WbsEstimateRollup> buildWbsRollups(EstimationRun run,
                                                    List<WbsNode> nodes,
                                                    List<TaskEstimateSnapshot> taskSnapshots) {
        Map<UUID, Set<UUID>> descendants = new HashMap<>();
        for (WbsNode node : nodes) {
            if (node.status() == WbsNodeStatus.ARCHIVED) {
                continue;
            }
            Set<UUID> desc = new HashSet<>();
            desc.add(node.id());
            collectDescendants(node.id(), nodes, desc, new HashSet<>());
            descendants.put(node.id(), desc);
        }

        List<WbsEstimateRollup> result = new ArrayList<>();
        for (WbsNode node : nodes) {
            if (node.status() == WbsNodeStatus.ARCHIVED) {
                continue;
            }
            Set<UUID> subtree = descendants.getOrDefault(node.id(), Set.of(node.id()));
            List<TaskEstimateSnapshot> inSubtree = taskSnapshots.stream()
                    .filter(s -> s.wbsNodeId() != null && subtree.contains(s.wbsNodeId()))
                    .toList();
            result.add(rollupWbs(run, node, inSubtree));
        }
        return result;
    }

    private void collectDescendants(UUID parentId, List<WbsNode> nodes, Set<UUID> acc, Set<UUID> visiting) {
        if (!visiting.add(parentId)) {
            throw new AppException(EstimationErrorCatalog.ESTIMATION_WBS_CYCLE_DETECTED);
        }
        for (WbsNode child : nodes) {
            if (parentId.equals(child.parentId()) && child.status() != WbsNodeStatus.ARCHIVED) {
                if (acc.add(child.id())) {
                    collectDescendants(child.id(), nodes, acc, visiting);
                }
            }
        }
        visiting.remove(parentId);
    }

    private WbsEstimateRollup rollupWbs(EstimationRun run, WbsNode node, List<TaskEstimateSnapshot> inSubtree) {
        Aggregates a = aggregate(inSubtree);
        return WbsEstimateRollup.create(
                run.id(), run.projectId(), node.id(), node.parentId(), node.level(),
                a.taskCount, a.includedTaskCount, a.unresolvedTaskCount,
                a.totalHours, a.totalLabor, a.totalBilling, a.currency);
    }

    private List<PhaseEstimateRollup> buildPhaseRollups(EstimationRun run,
                                                        List<TaskEstimateSnapshot> taskSnapshots) {
        Map<UUID, List<TaskEstimateSnapshot>> byPhase = taskSnapshots.stream()
                .filter(s -> s.projectPhaseId() != null)
                .collect(Collectors.groupingBy(TaskEstimateSnapshot::projectPhaseId));
        List<PhaseEstimateRollup> result = new ArrayList<>();
        for (Map.Entry<UUID, List<TaskEstimateSnapshot>> entry : byPhase.entrySet()) {
            Aggregates a = aggregate(entry.getValue());
            result.add(PhaseEstimateRollup.create(
                    run.id(), run.projectId(), entry.getKey(),
                    a.taskCount, a.includedTaskCount, a.unresolvedTaskCount,
                    a.totalHours, a.totalLabor, a.totalBilling, a.currency));
        }
        return result;
    }

    private ProjectEstimateSummary buildSummary(EstimationRun run, List<TaskEstimateSnapshot> taskSnapshots) {
        int total = taskSnapshots.size();
        int excluded = (int) taskSnapshots.stream().filter(s -> s.status() == TaskSnapshotStatus.EXCLUDED).count();
        int unestimated = (int) taskSnapshots.stream().filter(s -> s.status() == TaskSnapshotStatus.TASK_UNESTIMATED).count();
        int unresolvedRole = (int) taskSnapshots.stream().filter(s -> s.status() == TaskSnapshotStatus.ROLE_UNRESOLVED).count();
        int unresolvedRate = (int) taskSnapshots.stream().filter(s -> s.status() == TaskSnapshotStatus.RATE_UNRESOLVED).count();
        List<TaskEstimateSnapshot> included = taskSnapshots.stream()
                .filter(s -> s.status() != TaskSnapshotStatus.EXCLUDED)
                .toList();
        Aggregates a = aggregate(included);
        BigDecimal avgCost = a.totalHours.compareTo(BigDecimal.ZERO) > 0 && a.totalLabor != null
                ? a.totalLabor.divide(a.totalHours, MONEY_SCALE, RoundingMode.HALF_UP) : null;
        BigDecimal avgBilling = a.totalHours.compareTo(BigDecimal.ZERO) > 0 && a.totalBilling != null
                ? a.totalBilling.divide(a.totalHours, MONEY_SCALE, RoundingMode.HALF_UP) : null;
        int warnings = unresolvedRole + unresolvedRate + unestimated;
        return ProjectEstimateSummary.create(
                run.id(), run.projectId(), total, included.size(), excluded, unestimated,
                unresolvedRole, unresolvedRate, a.totalHours, a.totalLabor, a.totalBilling,
                avgCost, avgBilling, a.currency, warnings);
    }

    private Aggregates aggregate(List<TaskEstimateSnapshot> list) {
        int taskCount = list.size();
        int included = (int) list.stream().filter(s -> s.status() != TaskSnapshotStatus.EXCLUDED).count();
        int unresolved = (int) list.stream()
                .filter(s -> s.status() == TaskSnapshotStatus.ROLE_UNRESOLVED
                        || s.status() == TaskSnapshotStatus.RATE_UNRESOLVED)
                .count();
        BigDecimal hours = BigDecimal.ZERO;
        BigDecimal labor = BigDecimal.ZERO;
        BigDecimal billing = BigDecimal.ZERO;
        boolean anyLabor = false;
        boolean anyBilling = false;
        String currency = null;
        for (TaskEstimateSnapshot s : list) {
            if (s.status() == TaskSnapshotStatus.EXCLUDED) {
                continue;
            }
            if (s.estimateHours() != null) {
                hours = hours.add(s.estimateHours());
            }
            if (s.estimatedLaborCost() != null) {
                labor = labor.add(s.estimatedLaborCost());
                anyLabor = true;
            }
            if (s.estimatedBillingPreview() != null) {
                billing = billing.add(s.estimatedBillingPreview());
                anyBilling = true;
            }
            if (currency == null && s.currencyCode() != null) {
                currency = s.currencyCode();
            }
        }
        return new Aggregates(taskCount, included, unresolved,
                hours.setScale(2, RoundingMode.HALF_UP),
                anyLabor ? labor.setScale(MONEY_SCALE, RoundingMode.HALF_UP) : null,
                anyBilling ? billing.setScale(MONEY_SCALE, RoundingMode.HALF_UP) : null,
                currency);
    }

    private record Aggregates(int taskCount, int includedTaskCount, int unresolvedTaskCount,
                              BigDecimal totalHours, BigDecimal totalLabor, BigDecimal totalBilling,
                              String currency) {}
}
