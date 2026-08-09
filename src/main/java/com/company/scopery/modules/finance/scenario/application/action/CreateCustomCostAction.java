package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.command.CreateCustomCostCommand;
import com.company.scopery.modules.finance.scenario.application.response.CustomCostResponse;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCost;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCostRepository;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateCustomCostAction {

    private final FinanceScenarioRepository scenarios;
    private final CustomCostRepository customCosts;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public CreateCustomCostAction(FinanceScenarioRepository scenarios,
                                  CustomCostRepository customCosts,
                                  RecalculateFinanceSummaryAction recalculate,
                                  FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.customCosts = customCosts;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CustomCostResponse execute(CreateCustomCostCommand command) {
        scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(command.scenarioId()));

        CustomCost cost = CustomCost.create(
                command.scenarioId(),
                command.projectPhaseId(),
                command.category(),
                command.name(),
                command.description(),
                command.amount(),
                command.currencyCode(),
                command.costDate());

        CustomCost saved = customCosts.save(cost);
        recalculate.execute(command.scenarioId(), command.projectId());

        activityLogger.log(FinanceEntityTypes.CUSTOM_COST, saved.id(),
                FinanceActivityActions.CREATE_CUSTOM_COST,
                "Custom cost created: " + saved.name());

        return CustomCostResponse.from(saved);
    }
}
