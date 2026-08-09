package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.command.UpdateFinanceScenarioCommand;
import com.company.scopery.modules.finance.scenario.application.response.FinanceScenarioResponse;
import com.company.scopery.modules.finance.scenario.domain.enums.CostAdjustmentMethod;
import com.company.scopery.modules.finance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateFinanceScenarioAction {

    private final FinanceScenarioRepository scenarios;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public UpdateFinanceScenarioAction(FinanceScenarioRepository scenarios,
                                       RecalculateFinanceSummaryAction recalculate,
                                       FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FinanceScenarioResponse execute(UpdateFinanceScenarioCommand command) {
        FinanceScenario existing = scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(command.scenarioId()));

        RevenueSplitMethod revenueSplitMethod = command.revenueSplitMethod() != null
                ? parseRevenueSplitMethod(command.revenueSplitMethod())
                : existing.revenueSplitMethod();

        CostAdjustmentMethod contingencyMethod = command.contingencyMethod() != null
                ? parseCostAdjustmentMethod(command.contingencyMethod(), existing.contingencyMethod())
                : existing.contingencyMethod();

        CostAdjustmentMethod overheadMethod = command.overheadMethod() != null
                ? parseCostAdjustmentMethod(command.overheadMethod(), existing.overheadMethod())
                : existing.overheadMethod();

        FinanceScenario updated = new FinanceScenario(
                existing.id(),
                existing.projectId(),
                existing.workspaceId(),
                existing.estimationRunId(),
                existing.code(),
                command.name() != null ? command.name() : existing.name(),
                command.description() != null ? command.description() : existing.description(),
                existing.scenarioVersion(),
                existing.status(),
                existing.currencyCode(),
                command.plannedRevenue() != null ? command.plannedRevenue() : existing.plannedRevenue(),
                revenueSplitMethod,
                contingencyMethod,
                command.contingencyPercent() != null ? command.contingencyPercent() : existing.contingencyPercent(),
                command.contingencyFixedAmount() != null ? command.contingencyFixedAmount() : existing.contingencyFixedAmount(),
                overheadMethod,
                command.overheadPercent() != null ? command.overheadPercent() : existing.overheadPercent(),
                command.overheadFixedAmount() != null ? command.overheadFixedAmount() : existing.overheadFixedAmount(),
                command.targetMarginPercent() != null ? command.targetMarginPercent() : existing.targetMarginPercent(),
                command.assumptionsJson() != null ? command.assumptionsJson() : existing.assumptionsJson(),
                existing.currentFlag(),
                existing.approvedAt(),
                existing.formulaVersion(),
                existing.version(),
                existing.createdAt(),
                existing.updatedAt()
        );

        FinanceScenario saved = scenarios.save(updated);
        recalculate.execute(saved.id(), saved.projectId());

        activityLogger.log(FinanceEntityTypes.FINANCE_SCENARIO, saved.id(),
                FinanceActivityActions.UPDATE_SCENARIO,
                "Finance scenario updated: " + saved.code());

        return FinanceScenarioResponse.from(scenarios.findById(saved.id()).orElse(saved));
    }

    private RevenueSplitMethod parseRevenueSplitMethod(String value) {
        if (value == null || value.isBlank()) return null;
        try { return RevenueSplitMethod.valueOf(value); } catch (IllegalArgumentException e) { return null; }
    }

    private CostAdjustmentMethod parseCostAdjustmentMethod(String value, CostAdjustmentMethod defaultVal) {
        if (value == null || value.isBlank()) return defaultVal;
        try { return CostAdjustmentMethod.valueOf(value); } catch (IllegalArgumentException e) { return defaultVal; }
    }
}
