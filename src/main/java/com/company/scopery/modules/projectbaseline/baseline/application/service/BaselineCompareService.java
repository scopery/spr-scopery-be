package com.company.scopery.modules.projectbaseline.baseline.application.service;

import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.projectbaseline.baseline.application.response.BaselineCompareResponse;
import com.company.scopery.modules.projectbaseline.baseline.application.response.BaselineSummaryDto;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class BaselineCompareService {

    private final ProjectBaselineRepository baselines;
    private final BaselineSnapshotParser parser;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectRepository projects;
    private final ProjectPhaseRepository phases;
    private final WbsNodeRepository wbsNodes;
    private final TaskRepository tasks;
    private final ProjectFinanceScenarioRepository financeScenarios;
    private final ProjectFinanceSummaryRepository financeSummaries;
    private final QuoteVersionRepository quoteVersions;
    private final QuoteSummaryRepository quoteSummaries;

    public BaselineCompareService(ProjectBaselineRepository baselines,
                                   BaselineSnapshotParser parser,
                                   ProjectBaselineAuthorizationService authorization,
                                   ProjectRepository projects,
                                   ProjectPhaseRepository phases,
                                   WbsNodeRepository wbsNodes,
                                   TaskRepository tasks,
                                   ProjectFinanceScenarioRepository financeScenarios,
                                   ProjectFinanceSummaryRepository financeSummaries,
                                   QuoteVersionRepository quoteVersions,
                                   QuoteSummaryRepository quoteSummaries) {
        this.baselines = baselines;
        this.parser = parser;
        this.authorization = authorization;
        this.projects = projects;
        this.phases = phases;
        this.wbsNodes = wbsNodes;
        this.tasks = tasks;
        this.financeScenarios = financeScenarios;
        this.financeSummaries = financeSummaries;
        this.quoteVersions = quoteVersions;
        this.quoteSummaries = quoteSummaries;
    }

    @Transactional(readOnly = true)
    public BaselineCompareResponse compare(UUID projectId, UUID baselineId) {
        authorization.requireBaselineView(projectId);
        ProjectBaseline baseline = baselines.findByIdAndProjectId(baselineId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(baselineId));

        BaselineSummaryDto leftSummary = parser.parseSummary(baseline.summaryJson(), baseline.snapshotJson());
        String leftLabel = "BL-" + String.format("%02d", baseline.baselineNumber()) + " (" + baseline.status().name() + ")";

        BaselineSummaryDto rightSummary = buildLiveSummary(projectId);
        String rightLabel = "Current Plan";

        List<BaselineCompareResponse.DeltaItemDto> deltas = computeDeltas(leftSummary, rightSummary);
        BaselineCompareResponse.ChangeCountsDto changeCounts = computeChangeCounts(baseline.snapshotJson(), projectId);
        List<String> highlights = computeHighlights(deltas, changeCounts);

        return new BaselineCompareResponse(
                new BaselineCompareResponse.SideDto(leftLabel, leftSummary),
                new BaselineCompareResponse.SideDto(rightLabel, rightSummary),
                deltas, changeCounts, highlights
        );
    }

    private BaselineSummaryDto buildLiveSummary(UUID projectId) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));

        int phaseCount = phases.findAllByProjectId(projectId).size();
        int wbsCount = wbsNodes.findAllByProjectId(projectId).size();
        List<Task> liveTasks = tasks.findAllByProjectId(projectId);
        int taskCount = liveTasks.size();

        BigDecimal estimateHours = liveTasks.stream()
                .map(Task::estimateHours)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (estimateHours.compareTo(BigDecimal.ZERO) == 0) estimateHours = null;

        // Dates from phases
        String plannedStartDate = phases.findAllByProjectId(projectId).stream()
                .map(p -> p.plannedStartDate())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .map(LocalDate::toString)
                .orElse(null);
        String plannedEndDate = phases.findAllByProjectId(projectId).stream()
                .map(p -> p.plannedEndDate())
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .map(LocalDate::toString)
                .orElse(null);

        // Finance
        BigDecimal revenue = null, directCost = null, overhead = null, grossMargin = null, pbt = null;
        String currencyCode = null;

        Optional<ProjectFinanceScenario> currentScenario = financeScenarios.findCurrentByProjectId(projectId);
        if (currentScenario.isPresent()) {
            currencyCode = currentScenario.get().currencyCode();
            Optional<ProjectFinanceSummary> finSummary = financeSummaries.findByScenarioId(currentScenario.get().id());
            if (finSummary.isPresent()) {
                ProjectFinanceSummary s = finSummary.get();
                revenue = s.plannedRevenue();
                directCost = s.totalDirectCost();
                overhead = s.totalOverhead();
                grossMargin = s.grossMargin();
                pbt = s.profitBeforeTax();
                if (currencyCode == null) currencyCode = s.currencyCode();
            }
        }

        // Quote
        BigDecimal totalQuotedAmount = null, targetMarginPercent = null;
        UUID currentQuoteVersionId = project.currentQuoteVersionId();
        if (currentQuoteVersionId != null) {
            Optional<QuoteVersion> qv = quoteVersions.findById(currentQuoteVersionId);
            if (qv.isPresent()) {
                Optional<QuoteSummary> qs = quoteSummaries.findByQuoteVersionId(qv.get().id());
                if (qs.isPresent()) {
                    totalQuotedAmount = qs.get().totalQuotedAmount();
                    targetMarginPercent = qs.get().targetMarginPercent();
                    if (currencyCode == null) currencyCode = qs.get().currencyCode();
                }
            }
        }

        return new BaselineSummaryDto(
                phaseCount, wbsCount, taskCount, 0, 0,
                plannedStartDate, plannedEndDate, estimateHours,
                revenue, directCost, overhead, grossMargin, pbt, currencyCode,
                totalQuotedAmount, targetMarginPercent
        );
    }

    private List<BaselineCompareResponse.DeltaItemDto> computeDeltas(BaselineSummaryDto left, BaselineSummaryDto right) {
        List<BaselineCompareResponse.DeltaItemDto> deltas = new ArrayList<>();
        deltas.add(numericDelta("phaseCount", "Phase Count",
                BigDecimal.valueOf(left.phaseCount()), BigDecimal.valueOf(right.phaseCount())));
        deltas.add(numericDelta("wbsCount", "WBS Node Count",
                BigDecimal.valueOf(left.wbsCount()), BigDecimal.valueOf(right.wbsCount())));
        deltas.add(numericDelta("taskCount", "Task Count",
                BigDecimal.valueOf(left.taskCount()), BigDecimal.valueOf(right.taskCount())));
        deltas.add(numericDelta("estimateHours", "Estimate Hours", left.estimateHours(), right.estimateHours()));
        deltas.add(numericDelta("revenue", "Revenue", left.revenue(), right.revenue()));
        deltas.add(numericDelta("directCost", "Direct Cost", left.directCost(), right.directCost()));
        deltas.add(numericDelta("grossMargin", "Gross Margin", left.grossMargin(), right.grossMargin()));
        deltas.add(numericDelta("pbt", "Profit Before Tax", left.pbt(), right.pbt()));
        deltas.add(numericDelta("totalQuotedAmount", "Total Quoted Amount",
                left.totalQuotedAmount(), right.totalQuotedAmount()));
        return deltas;
    }

    private BaselineCompareResponse.DeltaItemDto numericDelta(String field, String label,
                                                               BigDecimal baseline, BigDecimal current) {
        String direction;
        if (baseline == null && current == null) {
            direction = "UNCHANGED";
        } else if (baseline == null) {
            direction = "INCREASED";
        } else if (current == null) {
            direction = "DECREASED";
        } else {
            int cmp = current.compareTo(baseline);
            if (cmp > 0) direction = "INCREASED";
            else if (cmp < 0) direction = "DECREASED";
            else direction = "UNCHANGED";
        }
        return new BaselineCompareResponse.DeltaItemDto(field, label, baseline, current, direction);
    }

    private BaselineCompareResponse.ChangeCountsDto computeChangeCounts(String snapshotJson, UUID projectId) {
        Set<UUID> frozenPhaseIds = extractIds(parser.parseListOfMaps(snapshotJson, "phases"));
        Set<UUID> frozenWbsIds = extractIds(parser.parseListOfMaps(snapshotJson, "wbs"));
        Set<UUID> frozenTaskIds = extractIds(parser.parseListOfMaps(snapshotJson, "tasks"));

        Set<UUID> livePhaseIds = extractDomainIds(phases.findAllByProjectId(projectId));
        Set<UUID> liveWbsIds = extractDomainIds(wbsNodes.findAllByProjectId(projectId));
        Set<UUID> liveTaskIds = extractDomainIds(tasks.findAllByProjectId(projectId));

        int phasesAdded = countAdded(livePhaseIds, frozenPhaseIds);
        int phasesRemoved = countRemoved(livePhaseIds, frozenPhaseIds);
        int wbsAdded = countAdded(liveWbsIds, frozenWbsIds);
        int wbsRemoved = countRemoved(liveWbsIds, frozenWbsIds);
        int tasksAdded = countAdded(liveTaskIds, frozenTaskIds);
        int tasksRemoved = countRemoved(liveTaskIds, frozenTaskIds);

        return new BaselineCompareResponse.ChangeCountsDto(
                phasesAdded, phasesRemoved, wbsAdded, wbsRemoved, tasksAdded, tasksRemoved, 0, 0);
    }

    private List<String> computeHighlights(List<BaselineCompareResponse.DeltaItemDto> deltas,
                                            BaselineCompareResponse.ChangeCountsDto changeCounts) {
        List<ScoredHighlight> scored = new ArrayList<>();

        if (changeCounts.tasksAdded() > 0)
            scored.add(new ScoredHighlight("+" + changeCounts.tasksAdded() + " task(s) added", changeCounts.tasksAdded()));
        if (changeCounts.tasksRemoved() > 0)
            scored.add(new ScoredHighlight("-" + changeCounts.tasksRemoved() + " task(s) removed", changeCounts.tasksRemoved()));
        if (changeCounts.wbsAdded() > 0)
            scored.add(new ScoredHighlight("+" + changeCounts.wbsAdded() + " WBS node(s) added", changeCounts.wbsAdded()));
        if (changeCounts.wbsRemoved() > 0)
            scored.add(new ScoredHighlight("-" + changeCounts.wbsRemoved() + " WBS node(s) removed", changeCounts.wbsRemoved()));
        if (changeCounts.phasesAdded() > 0)
            scored.add(new ScoredHighlight("+" + changeCounts.phasesAdded() + " phase(s) added", changeCounts.phasesAdded()));
        if (changeCounts.phasesRemoved() > 0)
            scored.add(new ScoredHighlight("-" + changeCounts.phasesRemoved() + " phase(s) removed", changeCounts.phasesRemoved()));

        for (BaselineCompareResponse.DeltaItemDto delta : deltas) {
            if ("UNCHANGED".equals(delta.direction())) continue;
            if (delta.baseline() instanceof BigDecimal bl && delta.current() instanceof BigDecimal cur
                    && bl.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal pct = cur.subtract(bl).divide(bl, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);
                String sign = pct.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
                scored.add(new ScoredHighlight(
                        delta.label() + " " + delta.direction().toLowerCase() + " by " + sign + pct + "%",
                        pct.abs().intValue()
                ));
            }
        }

        return scored.stream()
                .sorted(Comparator.comparingInt(ScoredHighlight::score).reversed())
                .limit(5)
                .map(ScoredHighlight::text)
                .toList();
    }

    private Set<UUID> extractIds(List<Map<String, Object>> items) {
        Set<UUID> ids = new LinkedHashSet<>();
        for (Map<String, Object> item : items) {
            Object idObj = item.get("id");
            if (idObj == null) continue;
            try { ids.add(UUID.fromString(idObj.toString())); } catch (IllegalArgumentException ignored) {}
        }
        return ids;
    }

    private <T> Set<UUID> extractDomainIds(List<T> items) {
        Set<UUID> ids = new LinkedHashSet<>();
        for (T item : items) {
            try {
                java.lang.reflect.Method m = item.getClass().getMethod("id");
                Object val = m.invoke(item);
                if (val instanceof UUID u) ids.add(u);
            } catch (Exception ignored) {}
        }
        return ids;
    }

    private int countAdded(Set<UUID> live, Set<UUID> frozen) {
        return (int) live.stream().filter(id -> !frozen.contains(id)).count();
    }

    private int countRemoved(Set<UUID> live, Set<UUID> frozen) {
        return (int) frozen.stream().filter(id -> !live.contains(id)).count();
    }

    private record ScoredHighlight(String text, int score) {}
}
