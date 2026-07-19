package com.company.scopery.modules.projectbaseline.baseline.application.service;

import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quoteversion.domain.enums.QuoteVersionStatus;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BaselineSnapshotService {

    private final ObjectMapper objectMapper;
    private final ProjectPhaseRepository phases;
    private final WbsNodeRepository wbsNodes;
    private final TaskRepository tasks;
    private final TaskDependencyRepository dependencies;
    private final ProjectMilestoneRepository milestones;
    private final ScheduleRunRepository scheduleRuns;
    private final EstimationRunRepository estimationRuns;
    private final ProjectFinanceScenarioRepository financeScenarios;
    private final ProjectFinanceSummaryRepository financeSummaries;
    private final QuoteVersionRepository quoteVersions;
    private final QuoteSummaryRepository quoteSummaries;

    public BaselineSnapshotService(ObjectMapper objectMapper,
                                   ProjectPhaseRepository phases,
                                   WbsNodeRepository wbsNodes,
                                   TaskRepository tasks,
                                   TaskDependencyRepository dependencies,
                                   ProjectMilestoneRepository milestones,
                                   ScheduleRunRepository scheduleRuns,
                                   EstimationRunRepository estimationRuns,
                                   ProjectFinanceScenarioRepository financeScenarios,
                                   ProjectFinanceSummaryRepository financeSummaries,
                                   QuoteVersionRepository quoteVersions,
                                   QuoteSummaryRepository quoteSummaries) {
        this.objectMapper = objectMapper;
        this.phases = phases;
        this.wbsNodes = wbsNodes;
        this.tasks = tasks;
        this.dependencies = dependencies;
        this.milestones = milestones;
        this.scheduleRuns = scheduleRuns;
        this.estimationRuns = estimationRuns;
        this.financeScenarios = financeScenarios;
        this.financeSummaries = financeSummaries;
        this.quoteVersions = quoteVersions;
        this.quoteSummaries = quoteSummaries;
    }

    public record SnapshotResult(String snapshotJson, String summaryJson) {}

    public SnapshotResult build(Project project,
                                UUID scheduleRunId,
                                UUID estimationRunId,
                                UUID financeScenarioId,
                                UUID quoteVersionId) {
        try {
            Map<String, Object> snapshot = new LinkedHashMap<>();
            Map<String, Object> summary = new LinkedHashMap<>();

            snapshot.put("project", Map.of(
                    "id", project.id(),
                    "code", project.code(),
                    "name", project.name(),
                    "status", project.status().name(),
                    "defaultCurrency", project.defaultCurrency() == null ? "" : project.defaultCurrency(),
                    "plannedStartDate", project.plannedStartDate() == null ? "" : project.plannedStartDate().toString(),
                    "plannedEndDate", project.plannedEndDate() == null ? "" : project.plannedEndDate().toString()
            ));

            List<Map<String, Object>> phaseSnaps = new ArrayList<>();
            for (ProjectPhase p : phases.findAllByProjectId(project.id())) {
                phaseSnaps.add(Map.of(
                        "id", p.id(),
                        "code", p.code() == null ? "" : p.code(),
                        "name", p.name() == null ? "" : p.name(),
                        "status", p.status().name(),
                        "displayOrder", p.displayOrder()
                ));
            }
            snapshot.put("phases", phaseSnaps);

            List<Map<String, Object>> wbsSnaps = new ArrayList<>();
            for (WbsNode n : wbsNodes.findAllByProjectId(project.id())) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", n.id());
                m.put("code", n.code());
                m.put("title", n.title());
                m.put("parentId", n.parentId());
                m.put("projectPhaseId", n.projectPhaseId());
                m.put("sortOrder", n.sortOrder());
                m.put("status", n.status().name());
                wbsSnaps.add(m);
            }
            snapshot.put("wbs", wbsSnaps);

            List<Map<String, Object>> taskSnaps = new ArrayList<>();
            for (Task t : tasks.findAllByProjectId(project.id())) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", t.id());
                m.put("code", t.code());
                m.put("title", t.title());
                m.put("status", t.status().name());
                m.put("projectPhaseId", t.projectPhaseId());
                m.put("wbsNodeId", t.wbsNodeId());
                m.put("estimateHours", t.estimateHours());
                m.put("dueDate", t.dueDate() == null ? null : t.dueDate().toString());
                m.put("inChargeUserId", t.inChargeUserId());
                m.put("plannedRoleCode", t.plannedRoleCode());
                taskSnaps.add(m);
            }
            snapshot.put("tasks", taskSnaps);

            List<Map<String, Object>> depSnaps = new ArrayList<>();
            for (TaskDependency d : dependencies.findActiveByProjectId(project.id())) {
                depSnaps.add(Map.of(
                        "id", d.id(),
                        "predecessorTaskId", d.predecessorTaskId(),
                        "successorTaskId", d.successorTaskId(),
                        "dependencyType", d.dependencyType().name(),
                        "lagDays", d.lagDays()
                ));
            }
            snapshot.put("dependencies", depSnaps);

            List<Map<String, Object>> msSnaps = new ArrayList<>();
            for (ProjectMilestone ms : milestones.findAllByProjectId(project.id())) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", ms.id());
                m.put("name", ms.name());
                m.put("status", ms.status().name());
                m.put("milestoneDate", ms.milestoneDate() == null ? null : ms.milestoneDate().toString());
                msSnaps.add(m);
            }
            snapshot.put("milestones", msSnaps);

            summary.put("phaseCount", phaseSnaps.size());
            summary.put("wbsCount", wbsSnaps.size());
            summary.put("taskCount", taskSnaps.size());
            summary.put("dependencyCount", depSnaps.size());
            summary.put("milestoneCount", msSnaps.size());

            snapshot.put("schedule", buildSchedule(project, scheduleRunId, summary));
            snapshot.put("estimation", buildEstimation(project, estimationRunId, summary));
            snapshot.put("finance", buildFinance(project, financeScenarioId, summary));
            snapshot.put("quote", buildQuote(project, quoteVersionId, summary));

            return new SnapshotResult(
                    objectMapper.writeValueAsString(snapshot),
                    objectMapper.writeValueAsString(summary));
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw ProjectBaselineExceptions.snapshotFailed(e.getMessage());
        }
    }

    private Map<String, Object> buildSchedule(Project project, UUID scheduleRunId, Map<String, Object> summary) {
        Map<String, Object> section = new LinkedHashMap<>();
        if (scheduleRunId == null) {
            section.put("status", "MISSING");
            summary.put("scheduleStatus", "MISSING");
            return section;
        }
        ScheduleRun run = scheduleRuns.findById(scheduleRunId)
                .orElseThrow(() -> ProjectBaselineExceptions.sourceMismatch("scheduleRun", scheduleRunId, project.id()));
        if (!run.projectId().equals(project.id())) {
            throw ProjectBaselineExceptions.sourceMismatch("scheduleRun", scheduleRunId, project.id());
        }
        if (run.status() != ScheduleRunStatus.COMPLETED) {
            throw ProjectBaselineExceptions.scheduleNotCompleted(scheduleRunId);
        }
        section.put("status", "CAPTURED");
        section.put("scheduleRunId", run.id());
        section.put("runStatus", run.status().name());
        section.put("resultSummaryJson", run.resultSummaryJson());
        summary.put("scheduleStatus", "CAPTURED");
        summary.put("sourceScheduleRunId", run.id());
        return section;
    }

    private Map<String, Object> buildEstimation(Project project, UUID estimationRunId, Map<String, Object> summary) {
        Map<String, Object> section = new LinkedHashMap<>();
        if (estimationRunId == null) {
            section.put("status", "MISSING");
            summary.put("estimationStatus", "MISSING");
            return section;
        }
        EstimationRun run = estimationRuns.findById(estimationRunId)
                .orElseThrow(() -> ProjectBaselineExceptions.sourceMismatch("estimationRun", estimationRunId, project.id()));
        if (!run.projectId().equals(project.id())) {
            throw ProjectBaselineExceptions.sourceMismatch("estimationRun", estimationRunId, project.id());
        }
        if (run.status() != EstimationRunStatus.COMPLETED) {
            throw ProjectBaselineExceptions.estimationNotCompleted(estimationRunId);
        }
        section.put("status", "CAPTURED");
        section.put("estimationRunId", run.id());
        section.put("resultSummaryJson", run.resultSummaryJson());
        summary.put("estimationStatus", "CAPTURED");
        summary.put("sourceEstimationRunId", run.id());
        return section;
    }

    private Map<String, Object> buildFinance(Project project, UUID financeScenarioId, Map<String, Object> summary) {
        Map<String, Object> section = new LinkedHashMap<>();
        if (financeScenarioId == null) {
            section.put("status", "MISSING");
            summary.put("financeStatus", "MISSING");
            return section;
        }
        ProjectFinanceScenario scenario = financeScenarios.findByIdAndProjectId(financeScenarioId, project.id())
                .orElseThrow(() -> ProjectBaselineExceptions.sourceMismatch("financeScenario", financeScenarioId, project.id()));
        if (scenario.status() != FinanceScenarioStatus.APPROVED && !scenario.currentFlag()) {
            throw ProjectBaselineExceptions.financeNotApproved(financeScenarioId);
        }
        Optional<ProjectFinanceSummary> finSummary = financeSummaries.findByScenarioId(scenario.id());
        section.put("status", "CAPTURED");
        section.put("financeScenarioId", scenario.id());
        section.put("scenarioStatus", scenario.status().name());
        section.put("currentFlag", scenario.currentFlag());
        section.put("currencyCode", scenario.currencyCode());
        if (finSummary.isPresent()) {
            ProjectFinanceSummary s = finSummary.get();
            Map<String, Object> fin = new LinkedHashMap<>();
            fin.put("plannedRevenue", s.plannedRevenue());
            fin.put("directCost", s.totalDirectCost());
            fin.put("overhead", s.totalOverhead());
            fin.put("budgetOfCosts", s.budgetOfCosts());
            fin.put("grossMargin", s.grossMargin());
            fin.put("pbt", s.profitBeforeTax());
            fin.put("currencyCode", s.currencyCode());
            section.put("summary", fin);
            summary.put("finance", fin);
        }
        summary.put("financeStatus", "CAPTURED");
        summary.put("sourceFinanceScenarioId", scenario.id());
        return section;
    }

    private Map<String, Object> buildQuote(Project project, UUID quoteVersionId, Map<String, Object> summary) {
        Map<String, Object> section = new LinkedHashMap<>();
        if (quoteVersionId == null) {
            section.put("status", "NOT_APPLICABLE");
            summary.put("quoteStatus", "NOT_APPLICABLE");
            return section;
        }
        QuoteVersion version = quoteVersions.findById(quoteVersionId)
                .orElseThrow(() -> ProjectBaselineExceptions.sourceMismatch("quoteVersion", quoteVersionId, project.id()));
        if (!version.projectId().equals(project.id())) {
            throw ProjectBaselineExceptions.sourceMismatch("quoteVersion", quoteVersionId, project.id());
        }
        if (version.status() != QuoteVersionStatus.APPROVED
                && version.status() != QuoteVersionStatus.SENT
                && version.status() != QuoteVersionStatus.ACCEPTED) {
            throw ProjectBaselineExceptions.quoteNotApproved(quoteVersionId);
        }
        section.put("status", "CAPTURED");
        section.put("quoteVersionId", version.id());
        section.put("quoteId", version.quoteId());
        section.put("versionStatus", version.status().name());
        section.put("currencyCode", version.currencyCode());
        section.put("targetMarginPercent", version.targetMarginPercent());
        section.put("validUntil", version.validUntil() == null ? null : version.validUntil().toString());
        quoteSummaries.findByQuoteVersionId(version.id()).ifPresent(qs -> {
            Map<String, Object> q = new LinkedHashMap<>();
            q.put("totalQuotedAmount", qs.totalQuotedAmount());
            q.put("targetMarginPercent", qs.targetMarginPercent());
            q.put("currencyCode", qs.currencyCode());
            section.put("summary", q);
            summary.put("quote", q);
        });
        summary.put("quoteStatus", "CAPTURED");
        summary.put("sourceQuoteVersionId", version.id());
        return section;
    }
}
