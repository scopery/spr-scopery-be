package com.company.scopery.modules.reporting.dashboard.application.service;

import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollupRepository;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummary;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshot;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshotRepository;
import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollupRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.SchedulingIssueType;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssue;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssueRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleRiskStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpact;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpactRepository;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrderRepository;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeRequestStatus;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequest;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.reporting.dashboard.application.response.AiPlanningReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.BaselineVsCurrentReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.CapacityReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ChangeImpactReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.EstimationReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.FinanceReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.NotificationsReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectAttentionResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectDashboardResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectHealthResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectKpisResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.QuoteReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ScheduleRiskReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.TaskRiskReportResponse;
import com.company.scopery.modules.reporting.dashboard.domain.enums.HealthStatus;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectDashboardQueryService {
    public static final String HEALTH_FORMULA_VERSION = "v1";

    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final ProjectBaselineRepository baselines;
    private final ChangeRequestRepository changeRequests;
    private final ChangeImpactRepository changeImpacts;
    private final ChangeOrderRepository changeOrders;
    private final AiPlanningSuggestionRepository suggestions;
    private final ScheduleRunRepository scheduleRuns;
    private final TaskScheduleRepository taskSchedules;
    private final SchedulingIssueRepository schedulingIssues;
    private final ProjectResourceAllocationRepository allocations;
    private final EstimationRunRepository estimationRuns;
    private final ProjectEstimateSummaryRepository estimateSummaries;
    private final PhaseEstimateRollupRepository phaseRollups;
    private final WbsEstimateRollupRepository wbsRollups;
    private final TaskEstimateSnapshotRepository taskSnapshots;
    private final ProjectFinanceScenarioRepository financeScenarios;
    private final ProjectFinanceSummaryRepository financeSummaries;
    private final ProjectPhaseFinanceRepository phaseFinance;
    private final QuoteVersionRepository quoteVersions;
    private final QuoteSummaryRepository quoteSummaries;
    private final ReportingAuthorizationService authorization;
    private final ObjectMapper objectMapper;

    public ProjectDashboardQueryService(
            ProjectRepository projects,
            TaskRepository tasks,
            ProjectBaselineRepository baselines,
            ChangeRequestRepository changeRequests,
            ChangeImpactRepository changeImpacts,
            ChangeOrderRepository changeOrders,
            AiPlanningSuggestionRepository suggestions,
            ScheduleRunRepository scheduleRuns,
            TaskScheduleRepository taskSchedules,
            SchedulingIssueRepository schedulingIssues,
            ProjectResourceAllocationRepository allocations,
            EstimationRunRepository estimationRuns,
            ProjectEstimateSummaryRepository estimateSummaries,
            PhaseEstimateRollupRepository phaseRollups,
            WbsEstimateRollupRepository wbsRollups,
            TaskEstimateSnapshotRepository taskSnapshots,
            ProjectFinanceScenarioRepository financeScenarios,
            ProjectFinanceSummaryRepository financeSummaries,
            ProjectPhaseFinanceRepository phaseFinance,
            QuoteVersionRepository quoteVersions,
            QuoteSummaryRepository quoteSummaries,
            ReportingAuthorizationService authorization,
            ObjectMapper objectMapper) {
        this.projects = projects;
        this.tasks = tasks;
        this.baselines = baselines;
        this.changeRequests = changeRequests;
        this.changeImpacts = changeImpacts;
        this.changeOrders = changeOrders;
        this.suggestions = suggestions;
        this.scheduleRuns = scheduleRuns;
        this.taskSchedules = taskSchedules;
        this.schedulingIssues = schedulingIssues;
        this.allocations = allocations;
        this.estimationRuns = estimationRuns;
        this.estimateSummaries = estimateSummaries;
        this.phaseRollups = phaseRollups;
        this.wbsRollups = wbsRollups;
        this.taskSnapshots = taskSnapshots;
        this.financeScenarios = financeScenarios;
        this.financeSummaries = financeSummaries;
        this.phaseFinance = phaseFinance;
        this.quoteVersions = quoteVersions;
        this.quoteSummaries = quoteSummaries;
        this.authorization = authorization;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public ProjectDashboardResponse dashboard(UUID projectId) {
        authorization.requireDashboard(projectId);
        Project project = requireProject(projectId);
        Map<String, Object> taskRisk = computeTaskRisk(projectId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("project", Map.of(
                "id", project.id(),
                "code", project.code(),
                "name", project.name(),
                "status", project.status().name()));
        result.put("taskRisk", taskRisk);
        result.put("health", computeHealth(projectId, taskRisk));
        result.put("baseline", Map.of(
                "currentBaselineId", project.currentBaselineId() == null ? "" : project.currentBaselineId(),
                "hasCurrentBaseline", baselines.findCurrentByProjectId(projectId).isPresent()));
        result.put("changeRequests", Map.of("count", changeRequests.findByProjectId(projectId).size()));
        result.put("aiPlanning", computeAiPlanning(projectId));
        if (authorization.canViewFinance(projectId)) {
            result.put("finance", Map.of(
                    "available", true,
                    "currentFinanceScenarioId",
                    project.currentFinanceScenarioId() == null ? "" : project.currentFinanceScenarioId()));
        } else {
            result.put("finance", Map.of(
                    "available", true,
                    "detailsRedacted", true,
                    "reason", "PROJECT_FINANCE_VIEW_REQUIRED"));
        }
        if (authorization.canViewQuote(projectId)) {
            result.put("quote", Map.of(
                    "available", true,
                    "currentQuoteVersionId",
                    project.currentQuoteVersionId() == null ? "" : project.currentQuoteVersionId()));
        } else {
            result.put("quote", Map.of(
                    "available", true,
                    "detailsRedacted", true,
                    "reason", "QUOTE_VIEW_REQUIRED"));
        }
        return convert(result, ProjectDashboardResponse.class);
    }

    @Transactional(readOnly = true)
    public ProjectHealthResponse health(UUID projectId) {
        authorization.requireDashboard(projectId);
        return convert(computeHealth(projectId, computeTaskRisk(projectId)), ProjectHealthResponse.class);
    }

    @Transactional(readOnly = true)
    public ProjectKpisResponse kpis(UUID projectId) {
        authorization.requireDashboard(projectId);
        Map<String, Object> taskRisk = computeTaskRisk(projectId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("taskRisk", taskRisk);
        out.put("health", computeHealth(projectId, taskRisk));
        out.put("aiPlanning", computeAiPlanning(projectId));
        return convert(out, ProjectKpisResponse.class);
    }

    @Transactional(readOnly = true)
    public ProjectAttentionResponse attention(UUID projectId) {
        authorization.requireDashboard(projectId);
        Map<String, Object> taskRisk = computeTaskRisk(projectId);
        Map<String, Object> ai = computeAiPlanning(projectId);
        return convert(Map.of(
                "overdueTasks", taskRisk.get("overdueTasks"),
                "blockedTasks", taskRisk.get("blockedTasks"),
                "dueSoonTasks", taskRisk.get("dueSoonTasks"),
                "pendingAiReview", ai.get("pendingReviewCount"),
                "changeRequestCount", changeRequests.findByProjectId(projectId).size()),
                ProjectAttentionResponse.class);
    }

    @Transactional(readOnly = true)
    public TaskRiskReportResponse taskRisk(UUID projectId) {
        authorization.requireReportView(projectId);
        return convert(computeTaskRisk(projectId), TaskRiskReportResponse.class);
    }

    @Transactional(readOnly = true)
    public ScheduleRiskReportResponse scheduleRisk(UUID projectId) {
        authorization.requireReportView(projectId);
        Project project = requireProject(projectId);
        Map<String, Object> m = new LinkedHashMap<>();
        if (project.currentScheduleRunId() == null) {
            m.put("currentScheduleRunId", "");
            m.put("sourceAvailable", false);
            m.put("status", HealthStatus.UNKNOWN.name());
            return convert(m, ScheduleRiskReportResponse.class);
        }
        ScheduleRun run = scheduleRuns.findCurrent(projectId, project.currentScheduleRunId()).orElse(null);
        if (run == null) {
            m.put("currentScheduleRunId", project.currentScheduleRunId());
            m.put("sourceAvailable", false);
            m.put("status", HealthStatus.UNKNOWN.name());
            return convert(m, ScheduleRiskReportResponse.class);
        }
        List<TaskSchedule> schedules = taskSchedules.findAllByScheduleRunId(run.id());
        List<SchedulingIssue> issues = schedulingIssues.findAllByScheduleRunId(run.id());
        Map<String, Long> issueCountBySeverity = issues.stream()
                .collect(Collectors.groupingBy(i -> i.severity().name(), LinkedHashMap::new, Collectors.counting()));
        long scheduled = schedules.stream()
                .filter(s -> s.scheduleStatus() == TaskScheduleStatus.SCHEDULED
                        || s.scheduleStatus() == TaskScheduleStatus.PARTIALLY_SCHEDULED)
                .count();
        long unscheduled = schedules.stream()
                .filter(s -> s.scheduleStatus() == TaskScheduleStatus.UNSCHEDULED)
                .count();
        long atRisk = schedules.stream()
                .filter(s -> s.riskStatus() == TaskScheduleRiskStatus.AT_RISK
                        || s.riskStatus() == TaskScheduleRiskStatus.OVERDUE
                        || s.riskStatus() == TaskScheduleRiskStatus.BLOCKED_BY_DEPENDENCY)
                .count();
        long overdue = schedules.stream().filter(s -> s.riskStatus() == TaskScheduleRiskStatus.OVERDUE).count();
        BigDecimal gapTotal = schedules.stream()
                .map(TaskSchedule::dueDateCapacityGapHours)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDate forecastStart = schedules.stream()
                .map(TaskSchedule::estimatedStartDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(run.planningStartDate());
        LocalDate forecastFinish = schedules.stream()
                .map(TaskSchedule::estimatedFinishDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(run.planningEndDate());
        m.put("currentScheduleRunId", run.id());
        m.put("sourceAvailable", true);
        m.put("scheduleRunStatus", run.status().name());
        m.put("forecastStartDate", forecastStart == null ? "" : forecastStart.toString());
        m.put("forecastFinishDate", forecastFinish == null ? "" : forecastFinish.toString());
        m.put("scheduledTasks", scheduled);
        m.put("unscheduledTasks", unscheduled);
        m.put("atRiskTasks", atRisk);
        m.put("overdueTasks", overdue);
        m.put("dueDateCapacityGapTotal", gapTotal);
        m.put("issueCountBySeverity", issueCountBySeverity);
        m.put("dependencyCycleCount", issues.stream()
                .filter(i -> i.issueType() == SchedulingIssueType.TASK_DEPENDENCY_CYCLE).count());
        m.put("noCapacityTaskCount", issues.stream()
                .filter(i -> i.issueType() == SchedulingIssueType.TASK_NO_CAPACITY).count());
        return convert(m, ScheduleRiskReportResponse.class);
    }

    @Transactional(readOnly = true)
    public CapacityReportResponse capacity(UUID projectId) {
        authorization.requireReportView(projectId);
        List<ProjectResourceAllocation> active = allocations.findActiveByProjectId(projectId);
        Map<String, Object> m = new LinkedHashMap<>();
        if (active.isEmpty()) {
            m.put("sourceAvailable", false);
            m.put("allocatedUsers", 0);
            m.put("averageAllocationPercent", BigDecimal.ZERO);
            m.put("overAllocatedUsers", 0);
            m.put("allocatedCapacityHours", BigDecimal.ZERO);
            m.put("scheduledHours", BigDecimal.ZERO);
            m.put("capacityGapHours", BigDecimal.ZERO);
            m.put("userCapacityRows", List.of());
            m.put("privateLeaveDetailsMasked", true);
            return convert(m, CapacityReportResponse.class);
        }
        BigDecimal avg = active.stream()
                .map(a -> a.allocationPercent() == null ? BigDecimal.ZERO : a.allocationPercent())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(active.size()), 2, RoundingMode.HALF_UP);
        long over = active.stream()
                .filter(a -> a.allocationPercent() != null && a.allocationPercent().compareTo(new BigDecimal("100")) > 0)
                .count();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (ProjectResourceAllocation a : active) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("userId", a.userId());
            row.put("allocationPercent", a.allocationPercent());
            row.put("allocationType", a.allocationType() == null ? "" : a.allocationType().name());
            row.put("startDate", a.startDate() == null ? "" : a.startDate().toString());
            row.put("endDate", a.endDate() == null ? "" : a.endDate().toString());
            // Private leave/time-off details intentionally omitted.
            rows.add(row);
        }
        m.put("sourceAvailable", true);
        m.put("allocatedUsers", active.size());
        m.put("averageAllocationPercent", avg);
        m.put("overAllocatedUsers", over);
        m.put("allocatedCapacityHours", BigDecimal.ZERO);
        m.put("scheduledHours", BigDecimal.ZERO);
        m.put("capacityGapHours", BigDecimal.ZERO);
        m.put("userCapacityRows", rows);
        m.put("privateLeaveDetailsMasked", true);
        return convert(m, CapacityReportResponse.class);
    }

    @Transactional(readOnly = true)
    public EstimationReportResponse estimation(UUID projectId) {
        authorization.requireReportView(projectId);
        Project project = requireProject(projectId);
        Map<String, Object> m = new LinkedHashMap<>();
        EstimationRun run = resolveEstimationRun(project);
        if (run == null) {
            m.put("currentEstimationRunId", "");
            m.put("sourceAvailable", false);
            return convert(m, EstimationReportResponse.class);
        }
        ProjectEstimateSummary summary = estimateSummaries.findByEstimationRunId(run.id()).orElse(null);
        m.put("currentEstimationRunId", run.id());
        m.put("sourceAvailable", true);
        if (summary == null) {
            m.put("totalEstimateHours", BigDecimal.ZERO);
            m.put("totalLaborCost", BigDecimal.ZERO);
            m.put("totalBillingPreview", BigDecimal.ZERO);
            m.put("unestimatedTaskCount", 0);
            m.put("unresolvedRoleTaskCount", 0);
            m.put("unresolvedRateTaskCount", 0);
        } else {
            m.put("totalEstimateHours", summary.totalEstimateHours());
            m.put("totalLaborCost", summary.totalLaborCost());
            m.put("totalBillingPreview", summary.totalBillingPreview());
            m.put("unestimatedTaskCount", summary.unestimatedTaskCount());
            m.put("unresolvedRoleTaskCount", summary.unresolvedRoleTaskCount());
            m.put("unresolvedRateTaskCount", summary.unresolvedRateTaskCount());
        }
        m.put("estimateByPhase", phaseRollups.findAllByEstimationRunId(run.id()).stream().map(p -> Map.of(
                "projectPhaseId", p.projectPhaseId(),
                "totalEstimateHours", p.totalEstimateHours(),
                "totalLaborCost", p.totalLaborCost(),
                "totalBillingPreview", p.totalBillingPreview()
        )).toList());
        m.put("estimateByWbs", wbsRollups.findAllByEstimationRunId(run.id()).stream().map(w -> Map.of(
                "wbsNodeId", w.wbsNodeId(),
                "totalEstimateHours", w.totalEstimateHours(),
                "totalLaborCost", w.totalLaborCost(),
                "totalBillingPreview", w.totalBillingPreview()
        )).toList());
        Map<String, BigDecimal> byRole = new LinkedHashMap<>();
        for (TaskEstimateSnapshot snap : taskSnapshots.findAllByEstimationRunId(run.id())) {
            String role = snap.costRoleCode() == null ? "UNRESOLVED" : snap.costRoleCode();
            byRole.merge(role, snap.estimateHours() == null ? BigDecimal.ZERO : snap.estimateHours(), BigDecimal::add);
        }
        m.put("estimateByCostRole", byRole);
        return convert(m, EstimationReportResponse.class);
    }

    @Transactional(readOnly = true)
    public FinanceReportResponse finance(UUID projectId) {
        authorization.requireReportView(projectId);
        if (!authorization.canViewFinance(projectId)) {
            throw ReportingExceptions.financeAccessDenied();
        }
        Project project = requireProject(projectId);
        Map<String, Object> m = new LinkedHashMap<>();
        ProjectFinanceScenario scenario = resolveFinanceScenario(project);
        if (scenario == null) {
            m.put("currentFinanceScenarioId", "");
            m.put("sourceAvailable", false);
            return convert(m, FinanceReportResponse.class);
        }
        ProjectFinanceSummary summary = financeSummaries.findByScenarioId(scenario.id()).orElse(null);
        m.put("currentFinanceScenarioId", scenario.id());
        m.put("sourceAvailable", true);
        if (summary == null) {
            m.put("plannedRevenue", BigDecimal.ZERO);
            m.put("directCost", BigDecimal.ZERO);
            m.put("laborCost", BigDecimal.ZERO);
            m.put("customCost", BigDecimal.ZERO);
            m.put("vendorCost", BigDecimal.ZERO);
            m.put("contingency", BigDecimal.ZERO);
            m.put("overhead", BigDecimal.ZERO);
            m.put("budgetOfCosts", BigDecimal.ZERO);
            m.put("grossMargin", BigDecimal.ZERO);
            m.put("grossMarginPercent", BigDecimal.ZERO);
            m.put("profitBeforeTax", BigDecimal.ZERO);
            m.put("pbtPercent", BigDecimal.ZERO);
        } else {
            m.put("plannedRevenue", summary.plannedRevenue());
            m.put("directCost", summary.totalDirectCost());
            m.put("laborCost", summary.totalLaborCost());
            m.put("customCost", summary.totalCustomCost());
            m.put("vendorCost", summary.totalVendorCost());
            m.put("contingency", summary.totalContingency());
            m.put("overhead", summary.totalOverhead());
            m.put("budgetOfCosts", summary.budgetOfCosts());
            m.put("grossMargin", summary.grossMargin());
            m.put("grossMarginPercent", summary.grossMarginPercent());
            m.put("profitBeforeTax", summary.profitBeforeTax());
            m.put("pbtPercent", summary.pbtPercent());
        }
        m.put("phaseFinanceBreakdown", phaseFinance.findByScenarioId(scenario.id()).stream().map(p -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("projectPhaseId", p.projectPhaseId());
            row.put("phaseName", p.phaseNameSnapshot() == null ? "" : p.phaseNameSnapshot());
            row.put("plannedRevenue", p.plannedRevenue());
            row.put("directCost", p.directCost());
            row.put("grossMargin", p.grossMargin());
            row.put("pbtPercent", p.pbtPercent());
            return row;
        }).toList());
        return convert(m, FinanceReportResponse.class);
    }

    @Transactional(readOnly = true)
    public QuoteReportResponse quote(UUID projectId) {
        authorization.requireReportView(projectId);
        if (!authorization.canViewQuote(projectId)) {
            throw ReportingExceptions.quoteAccessDenied();
        }
        Project project = requireProject(projectId);
        Map<String, Object> m = new LinkedHashMap<>();
        if (project.currentQuoteVersionId() == null) {
            m.put("currentQuoteVersionId", "");
            m.put("sourceAvailable", false);
            return convert(m, QuoteReportResponse.class);
        }
        QuoteVersion version = quoteVersions.findById(project.currentQuoteVersionId()).orElse(null);
        if (version == null) {
            m.put("currentQuoteVersionId", project.currentQuoteVersionId());
            m.put("sourceAvailable", false);
            return convert(m, QuoteReportResponse.class);
        }
        QuoteSummary summary = quoteSummaries.findByQuoteVersionId(version.id()).orElse(null);
        m.put("currentQuoteVersionId", version.id());
        m.put("sourceAvailable", true);
        m.put("quoteStatus", version.status().name());
        m.put("targetMarginPercent", version.targetMarginPercent());
        m.put("discountAmount", version.discountAmount());
        m.put("validUntil", version.validUntil() == null ? "" : version.validUntil().toString());
        m.put("sentAt", version.sentAt() == null ? "" : version.sentAt().toString());
        m.put("acceptedAt", version.acceptedAt() == null ? "" : version.acceptedAt().toString());
        if (summary == null) {
            m.put("totalQuotedAmount", BigDecimal.ZERO);
            m.put("grossMarginPercent", BigDecimal.ZERO);
            m.put("pbtPercent", BigDecimal.ZERO);
        } else {
            m.put("totalQuotedAmount", summary.totalQuotedAmount());
            m.put("grossMarginPercent", summary.grossMarginPercent());
            m.put("pbtPercent", summary.pbtPercent());
            if (summary.discountAmount() != null) {
                m.put("discountAmount", summary.discountAmount());
            }
            if (summary.targetMarginPercent() != null) {
                m.put("targetMarginPercent", summary.targetMarginPercent());
            }
        }
        return convert(m, QuoteReportResponse.class);
    }

    @Transactional(readOnly = true)
    public BaselineVsCurrentReportResponse baselineVsCurrent(UUID projectId) {
        authorization.requireReportView(projectId);
        Project project = requireProject(projectId);
        Map<String, Object> m = new LinkedHashMap<>();
        ProjectBaseline baseline = baselines.findCurrentByProjectId(projectId).orElse(null);
        if (baseline == null) {
            m.put("sourceAvailable", false);
            m.put("currentBaselineId", "");
            return convert(m, BaselineVsCurrentReportResponse.class);
        }
        List<Task> currentTasks = tasks.findAllByProjectId(projectId).stream()
                .filter(t -> t.status() != TaskStatus.ARCHIVED)
                .toList();
        m.put("sourceAvailable", true);
        m.put("currentBaselineId", baseline.id());
        m.put("baselineNumber", baseline.baselineNumber());
        m.put("taskCountDelta", Map.of(
                "currentTaskCount", currentTasks.size(),
                "baselineScheduleRunId", baseline.sourceScheduleRunId() == null ? "" : baseline.sourceScheduleRunId(),
                "currentScheduleRunId", project.currentScheduleRunId() == null ? "" : project.currentScheduleRunId()));
        m.put("scheduleFinishDelta", Map.of(
                "baselineScheduleRunId", baseline.sourceScheduleRunId() == null ? "" : baseline.sourceScheduleRunId(),
                "currentScheduleRunId", project.currentScheduleRunId() == null ? "" : project.currentScheduleRunId()));
        m.put("estimateHoursDelta", Map.of(
                "baselineEstimationRunId", baseline.sourceEstimationRunId() == null ? "" : baseline.sourceEstimationRunId(),
                "currentEstimationRunId", project.currentEstimationRunId() == null ? "" : project.currentEstimationRunId()));
        m.put("changeRequestCount", changeRequests.findByProjectId(projectId).size());
        m.put("approvedChangeImpact", changeRequests.findByProjectId(projectId).stream()
                .filter(cr -> cr.status() == ChangeRequestStatus.APPROVED || cr.status() == ChangeRequestStatus.APPLIED)
                .count());
        long statusDiff = 0;
        long estimateDiff = 0;
        if (baseline.snapshotJson() != null && !baseline.snapshotJson().isBlank()) {
            try {
                Map<String, Object> snap = objectMapper.readValue(
                        baseline.snapshotJson(), new TypeReference<Map<String, Object>>() {});
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> baselineTasks = (List<Map<String, Object>>) snap.get("tasks");
                if (baselineTasks != null) {
                    Map<String, Map<String, Object>> byId = new LinkedHashMap<>();
                    for (Map<String, Object> bt : baselineTasks) {
                        Object id = bt.get("id");
                        if (id != null) {
                            byId.put(String.valueOf(id), bt);
                        }
                    }
                    for (Task current : currentTasks) {
                        Map<String, Object> base = byId.get(current.id().toString());
                        if (base == null) {
                            continue;
                        }
                        String baseStatus = String.valueOf(base.get("status"));
                        if (!baseStatus.equals(current.status().name())) {
                            statusDiff++;
                        }
                        Object baseEst = base.get("estimateHours");
                        if (baseEst != null && current.estimateHours() != null
                                && new BigDecimal(String.valueOf(baseEst)).compareTo(current.estimateHours()) != 0) {
                            estimateDiff++;
                        } else if (baseEst == null && current.estimateHours() != null) {
                            estimateDiff++;
                        } else if (baseEst != null && current.estimateHours() == null) {
                            estimateDiff++;
                        }
                    }
                }
            } catch (Exception ignored) {
                // keep zeros when snapshot parse fails
            }
        }
        m.put("tasksWithStatusDiff", statusDiff);
        m.put("tasksWithEstimateDiff", estimateDiff);
        if (authorization.canViewFinance(projectId)) {
            m.put("financeDelta", Map.of(
                    "baselineFinanceScenarioId",
                    baseline.sourceFinanceScenarioId() == null ? "" : baseline.sourceFinanceScenarioId(),
                    "currentFinanceScenarioId",
                    project.currentFinanceScenarioId() == null ? "" : project.currentFinanceScenarioId()));
        } else {
            m.put("financeDelta", Map.of("detailsRedacted", true, "reason", "PROJECT_FINANCE_VIEW_REQUIRED"));
        }
        if (authorization.canViewQuote(projectId)) {
            m.put("quoteDelta", Map.of(
                    "baselineQuoteVersionId",
                    baseline.sourceQuoteVersionId() == null ? "" : baseline.sourceQuoteVersionId(),
                    "currentQuoteVersionId",
                    project.currentQuoteVersionId() == null ? "" : project.currentQuoteVersionId()));
        } else {
            m.put("quoteDelta", Map.of("detailsRedacted", true, "reason", "QUOTE_VIEW_REQUIRED"));
        }
        return convert(m, BaselineVsCurrentReportResponse.class);
    }

    @Transactional(readOnly = true)
    public ChangeImpactReportResponse changeImpact(UUID projectId) {
        authorization.requireReportView(projectId);
        List<ChangeRequest> crs = changeRequests.findByProjectId(projectId);
        Map<ChangeRequestStatus, Long> byStatus = new EnumMap<>(ChangeRequestStatus.class);
        for (ChangeRequestStatus s : ChangeRequestStatus.values()) {
            byStatus.put(s, 0L);
        }
        for (ChangeRequest cr : crs) {
            byStatus.merge(cr.status(), 1L, Long::sum);
        }
        int scheduleDays = 0;
        BigDecimal estimateHours = BigDecimal.ZERO;
        BigDecimal costImpact = BigDecimal.ZERO;
        BigDecimal revenueImpact = BigDecimal.ZERO;
        BigDecimal marginImpact = BigDecimal.ZERO;
        long changeOrderCount = 0;
        for (ChangeRequest cr : crs) {
            changeOrderCount += changeOrders.findByChangeRequestId(cr.id()).size();
            ChangeImpact impact = changeImpacts.findByChangeRequestId(cr.id()).orElse(null);
            if (impact == null) {
                continue;
            }
            if (impact.scheduleImpactDays() != null) {
                scheduleDays += impact.scheduleImpactDays();
            }
            if (impact.estimateHoursImpact() != null) {
                estimateHours = estimateHours.add(impact.estimateHoursImpact());
            }
            if (impact.directCostImpact() != null) {
                costImpact = costImpact.add(impact.directCostImpact());
            }
            if (impact.laborCostImpact() != null) {
                costImpact = costImpact.add(impact.laborCostImpact());
            }
            if (impact.revenueImpact() != null) {
                revenueImpact = revenueImpact.add(impact.revenueImpact());
            }
            if (impact.grossMarginImpact() != null) {
                marginImpact = marginImpact.add(impact.grossMarginImpact());
            }
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("changeRequestCount", crs.size());
        m.put("submittedCount", byStatus.get(ChangeRequestStatus.SUBMITTED));
        m.put("approvedCount", byStatus.get(ChangeRequestStatus.APPROVED));
        m.put("rejectedCount", byStatus.get(ChangeRequestStatus.REJECTED));
        m.put("appliedCount", byStatus.get(ChangeRequestStatus.APPLIED));
        m.put("pendingApprovalCount", byStatus.get(ChangeRequestStatus.SUBMITTED));
        m.put("totalScheduleImpactDays", scheduleDays);
        m.put("totalEstimateHoursImpact", estimateHours);
        m.put("changeOrderCount", changeOrderCount);
        if (authorization.canViewFinance(projectId)) {
            m.put("totalCostImpact", costImpact);
            m.put("totalRevenueImpact", revenueImpact);
            m.put("totalMarginImpact", marginImpact);
            m.put("financeDetailsRedacted", false);
        } else {
            m.put("totalCostImpact", null);
            m.put("totalRevenueImpact", null);
            m.put("totalMarginImpact", null);
            m.put("financeDetailsRedacted", true);
        }
        return convert(m, ChangeImpactReportResponse.class);
    }

    @Transactional(readOnly = true)
    public NotificationsReportResponse notifications(UUID projectId) {
        authorization.requireReportView(projectId);
        Map<String, Object> taskRisk = computeTaskRisk(projectId);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("overdueAlerts", taskRisk.get("overdueTasks"));
        m.put("dueSoonAlerts", taskRisk.get("dueSoonTasks"));
        m.put("scheduleRiskAlerts", ((Number) taskRisk.get("atRiskTasks")).longValue());
        m.put("changeRequestAlerts", changeRequests.findByProjectId(projectId).size());
        m.put("unreadNotifications", 0);
        m.put("criticalAlerts", taskRisk.get("blockedTasks"));
        m.put("failedDeliveries", 0);
        return convert(m, NotificationsReportResponse.class);
    }

    @Transactional(readOnly = true)
    public AiPlanningReportResponse aiPlanning(UUID projectId) {
        authorization.requireReportView(projectId);
        return convert(computeAiPlanning(projectId), AiPlanningReportResponse.class);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> resolveReportData(String reportType, UUID projectId) {
        Object typed = switch (reportType) {
            case "TASK_RISK" -> taskRisk(projectId);
            case "SCHEDULE_RISK" -> scheduleRisk(projectId);
            case "CAPACITY" -> capacity(projectId);
            case "ESTIMATION" -> estimation(projectId);
            case "FINANCE" -> finance(projectId);
            case "QUOTE" -> quote(projectId);
            case "BASELINE_VS_CURRENT" -> baselineVsCurrent(projectId);
            case "CHANGE_IMPACT" -> changeImpact(projectId);
            case "NOTIFICATION" -> notifications(projectId);
            case "AI_PLANNING" -> aiPlanning(projectId);
            case "PROJECT_HEALTH" -> health(projectId);
            case "DASHBOARD" -> dashboard(projectId);
            default -> dashboard(projectId);
        };
        return objectMapper.convertValue(typed, new TypeReference<>() {});
    }

    private <T> T convert(Map<String, Object> map, Class<T> type) {
        return objectMapper.convertValue(map, type);
    }

    private Map<String, Object> computeHealth(UUID projectId, Map<String, Object> taskRisk) {
        long overdue = ((Number) taskRisk.get("overdueTasks")).longValue();
        long blocked = ((Number) taskRisk.get("blockedTasks")).longValue();
        long total = ((Number) taskRisk.get("totalTasks")).longValue();
        Project project = requireProject(projectId);
        HealthStatus status;
        if (project.currentScheduleRunId() != null) {
            ScheduleRun run = scheduleRuns.findCurrent(projectId, project.currentScheduleRunId()).orElse(null);
            if (run != null && run.status() == ScheduleRunStatus.FAILED) {
                status = HealthStatus.RED;
                return Map.of("status", status.name(), "formulaVersion", HEALTH_FORMULA_VERSION, "drivers", taskRisk);
            }
        }
        if (blocked > 0 || overdue > 5) {
            status = HealthStatus.RED;
        } else if (overdue > 0) {
            status = HealthStatus.YELLOW;
        } else if (total == 0) {
            status = HealthStatus.UNKNOWN;
        } else {
            status = HealthStatus.GREEN;
        }
        return Map.of("status", status.name(), "formulaVersion", HEALTH_FORMULA_VERSION, "drivers", taskRisk);
    }

    private Map<String, Object> computeTaskRisk(UUID projectId) {
        List<Task> all = tasks.findAllByProjectId(projectId).stream()
                .filter(t -> t.status() != TaskStatus.ARCHIVED)
                .toList();
        LocalDate today = LocalDate.now();
        long todo = all.stream().filter(t -> t.status() == TaskStatus.TODO).count();
        long inProgress = all.stream().filter(t -> t.status() == TaskStatus.IN_PROGRESS).count();
        long blocked = all.stream().filter(t -> t.status() == TaskStatus.BLOCKED).count();
        long completed = all.stream().filter(t -> t.status() == TaskStatus.DONE).count();
        long cancelled = all.stream().filter(t -> t.status() == TaskStatus.CANCELLED).count();
        long overdue = all.stream().filter(t -> t.dueDate() != null && t.dueDate().isBefore(today)
                && t.status() != TaskStatus.DONE && t.status() != TaskStatus.CANCELLED).count();
        long dueSoon = all.stream().filter(t -> t.dueDate() != null && !t.dueDate().isBefore(today)
                && !t.dueDate().isAfter(today.plusDays(2))
                && t.status() != TaskStatus.DONE && t.status() != TaskStatus.CANCELLED).count();
        long withoutEstimate = all.stream().filter(t -> t.estimateHours() == null).count();
        long withoutAssignee = all.stream().filter(t -> t.inChargeUserId() == null).count();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalTasks", all.size());
        m.put("todoTasks", todo);
        m.put("inProgressTasks", inProgress);
        m.put("blockedTasks", blocked);
        m.put("completedTasks", completed);
        m.put("cancelledTasks", cancelled);
        m.put("overdueTasks", overdue);
        m.put("dueSoonTasks", dueSoon);
        m.put("unscheduledTasks", all.stream()
                .filter(t -> t.plannedStartDate() == null && t.dueDate() == null).count());
        m.put("atRiskTasks", overdue + blocked);
        m.put("tasksWithoutEstimate", withoutEstimate);
        m.put("tasksWithoutAssignee", withoutAssignee);
        return m;
    }

    private Map<String, Object> computeAiPlanning(UUID projectId) {
        var list = suggestions.findByProjectId(projectId);
        long pending = list.stream()
                .filter(s -> s.status() == SuggestionStatus.GENERATED || s.status() == SuggestionStatus.UNDER_REVIEW)
                .count();
        long accepted = list.stream().filter(s -> s.status() == SuggestionStatus.ACCEPTED).count();
        long rejected = list.stream().filter(s -> s.status() == SuggestionStatus.REJECTED).count();
        long applied = list.stream()
                .filter(s -> s.status() == SuggestionStatus.APPLIED || s.status() == SuggestionStatus.PARTIALLY_APPLIED)
                .count();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("aiPlanningRuns", list.size());
        m.put("generatedSuggestions", list.size());
        m.put("pendingReviewCount", pending);
        m.put("acceptedSuggestions", accepted);
        m.put("rejectedSuggestions", rejected);
        m.put("appliedSuggestions", applied);
        return m;
    }

    private EstimationRun resolveEstimationRun(Project project) {
        if (project.currentEstimationRunId() != null) {
            return estimationRuns.findCurrent(project.id(), project.currentEstimationRunId())
                    .or(() -> estimationRuns.findById(project.currentEstimationRunId()))
                    .orElse(null);
        }
        return estimationRuns.findLatestCompletedByProjectId(project.id()).orElse(null);
    }

    private ProjectFinanceScenario resolveFinanceScenario(Project project) {
        if (project.currentFinanceScenarioId() != null) {
            return financeScenarios.findByIdAndProjectId(project.currentFinanceScenarioId(), project.id())
                    .or(() -> financeScenarios.findById(project.currentFinanceScenarioId()))
                    .orElse(null);
        }
        return financeScenarios.findCurrentByProjectId(project.id()).orElse(null);
    }

    private Project requireProject(UUID projectId) {
        return projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
    }
}
