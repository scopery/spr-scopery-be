package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.command.UpdatePhaseRevenueCommand;
import com.company.scopery.modules.finance.scenario.application.response.PhaseFinanceResponse;
import com.company.scopery.modules.finance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinance;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinanceRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdatePhaseRevenueAction {

    private final FinanceScenarioRepository scenarios;
    private final PhaseFinanceRepository phases;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public UpdatePhaseRevenueAction(FinanceScenarioRepository scenarios,
                                    PhaseFinanceRepository phases,
                                    RecalculateFinanceSummaryAction recalculate,
                                    FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.phases = phases;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public PhaseFinanceResponse execute(UpdatePhaseRevenueCommand command) {
        FinanceScenario scenario = scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(command.scenarioId()));

        PhaseFinance phase = phases.findByScenarioIdAndProjectPhaseId(command.scenarioId(), command.projectPhaseId())
                .orElseThrow(() -> FinanceExceptions.costNotFound(command.projectPhaseId()));

        // Force scenario to MANUAL split if updating phase revenue
        if (scenario.revenueSplitMethod() != RevenueSplitMethod.MANUAL) {
            FinanceScenario updated = new FinanceScenario(
                    scenario.id(), scenario.projectId(), scenario.workspaceId(), scenario.estimationRunId(),
                    scenario.code(), scenario.name(), scenario.description(), scenario.scenarioVersion(),
                    scenario.status(), scenario.currencyCode(), scenario.plannedRevenue(),
                    RevenueSplitMethod.MANUAL,
                    scenario.contingencyMethod(), scenario.contingencyPercent(), scenario.contingencyFixedAmount(),
                    scenario.overheadMethod(), scenario.overheadPercent(), scenario.overheadFixedAmount(),
                    scenario.targetMarginPercent(), scenario.assumptionsJson(), scenario.currentFlag(),
                    scenario.approvedAt(), scenario.formulaVersion(), scenario.version(),
                    scenario.createdAt(), scenario.updatedAt());
            scenarios.save(updated);
        }

        PhaseFinance updated = phase.withCalculatedValues(
                phase.customCost(),
                phase.vendorCost(),
                phase.contingencyAmount(),
                phase.directCost(),
                phase.overheadAmount(),
                phase.budgetOfCosts(),
                command.plannedRevenue(),
                command.revenuePercent(),
                phase.grossMargin(),
                phase.grossMarginPercent(),
                phase.profitBeforeTax(),
                phase.pbtPercent());

        PhaseFinance saved = phases.save(updated);
        recalculate.execute(command.scenarioId(), command.projectId());

        activityLogger.log(FinanceEntityTypes.FINANCE_SCENARIO, command.scenarioId(),
                FinanceActivityActions.UPDATE_PHASE_REVENUE,
                "Phase revenue updated for scenario: " + command.scenarioId());

        return PhaseFinanceResponse.from(saved);
    }
}
