package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollupRepository;
import com.company.scopery.modules.finance.scenario.application.command.CreateFinanceScenarioCommand;
import com.company.scopery.modules.finance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.finance.scenario.domain.enums.CostAdjustmentMethod;
import com.company.scopery.modules.finance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummary;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummaryRepository;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinance;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinanceRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class CreateFinanceScenarioAction {

    private final FinanceScenarioRepository scenarios;
    private final FinanceSummaryRepository summaries;
    private final PhaseFinanceRepository phases;
    private final EstimationRunRepository estimationRuns;
    private final PhaseEstimateRollupRepository phaseRollups;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public CreateFinanceScenarioAction(FinanceScenarioRepository scenarios,
                                       FinanceSummaryRepository summaries,
                                       PhaseFinanceRepository phases,
                                       EstimationRunRepository estimationRuns,
                                       PhaseEstimateRollupRepository phaseRollups,
                                       RecalculateFinanceSummaryAction recalculate,
                                       FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.summaries = summaries;
        this.phases = phases;
        this.estimationRuns = estimationRuns;
        this.phaseRollups = phaseRollups;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(CreateFinanceScenarioCommand command) {
        // 1. Check code uniqueness
        if (scenarios.existsByProjectIdAndCode(command.projectId(), command.code())) {
            throw FinanceExceptions.scenarioCodeExists(command.code());
        }

        // 2. Validate estimation run if provided
        if (command.estimationRunId() != null) {
            estimationRuns.findById(command.estimationRunId())
                    .orElseThrow(() -> FinanceExceptions.estimationRunNotFound(command.estimationRunId()));
        }

        // 3. Parse enums safely
        RevenueSplitMethod revenueSplitMethod = parseRevenueSplitMethod(command.revenueSplitMethod());
        CostAdjustmentMethod contingencyMethod = parseCostAdjustmentMethod(command.contingencyMethod(), CostAdjustmentMethod.NONE);
        CostAdjustmentMethod overheadMethod = parseCostAdjustmentMethod(command.overheadMethod(), CostAdjustmentMethod.NONE);

        // 4. Create scenario
        FinanceScenario scenario = FinanceScenario.create(
                command.projectId(),
                command.workspaceId(),
                command.estimationRunId(),
                command.code(),
                command.name(),
                command.description(),
                command.currencyCode(),
                command.plannedRevenue(),
                revenueSplitMethod,
                contingencyMethod,
                command.contingencyPercent(),
                command.contingencyFixedAmount(),
                overheadMethod,
                command.overheadPercent(),
                command.overheadFixedAmount(),
                command.targetMarginPercent(),
                command.assumptionsJson());

        // 5. Mark as current if requested
        if (command.markAsCurrent()) {
            scenarios.clearCurrentFlagForProject(command.projectId());
            scenario = scenario.markCurrent();
        }

        scenario = scenarios.save(scenario);
        final UUID scenarioId = scenario.id();

        // 6. Create empty finance summary
        FinanceSummary summary = FinanceSummary.create(scenarioId, command.projectId(), command.currencyCode());
        summaries.save(summary);

        // 7. Create PhaseFinance records from estimation rollups (if estimation run specified)
        if (command.estimationRunId() != null) {
            List<PhaseEstimateRollup> rollups = phaseRollups.findAllByEstimationRunId(command.estimationRunId());
            if (!rollups.isEmpty()) {
                List<PhaseFinance> phaseList = rollups.stream()
                        .map(r -> PhaseFinance.create(
                                scenarioId,
                                r.projectPhaseId(),
                                "Phase " + r.projectPhaseId(),
                                0,
                                r.totalEstimateHours(),
                                r.totalLaborCost()))
                        .toList();
                phases.saveAll(phaseList);
            }
        }

        // 8. Recalculate summary
        recalculate.execute(scenarioId, command.projectId());

        activityLogger.log(FinanceEntityTypes.FINANCE_SCENARIO, scenario.id(),
                FinanceActivityActions.CREATE_SCENARIO,
                "Finance scenario created: " + scenario.code());

        // Reload after recalculation
        FinanceScenario saved = scenarios.findById(scenarioId)
                .orElse(scenario);
        return FinanceScenarioResponse.from(saved);
    }

    private RevenueSplitMethod parseRevenueSplitMethod(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return RevenueSplitMethod.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private CostAdjustmentMethod parseCostAdjustmentMethod(String value, CostAdjustmentMethod defaultVal) {
        if (value == null || value.isBlank()) return defaultVal;
        try {
            return CostAdjustmentMethod.valueOf(value);
        } catch (IllegalArgumentException e) {
            return defaultVal;
        }
    }
}
