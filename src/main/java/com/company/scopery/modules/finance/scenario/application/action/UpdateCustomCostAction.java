package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.command.UpdateCustomCostCommand;
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
public class UpdateCustomCostAction {

    private final FinanceScenarioRepository scenarios;
    private final CustomCostRepository customCosts;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public UpdateCustomCostAction(FinanceScenarioRepository scenarios,
                                  CustomCostRepository customCosts,
                                  RecalculateFinanceSummaryAction recalculate,
                                  FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.customCosts = customCosts;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CustomCostResponse execute(UpdateCustomCostCommand command) {
        scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(command.scenarioId()));

        CustomCost existing = customCosts.findById(command.costId())
                .orElseThrow(() -> FinanceExceptions.costNotFound(command.costId()));

        CustomCost updated = new CustomCost(
                existing.id(),
                existing.financeScenarioId(),
                existing.projectPhaseId(),
                command.category() != null ? command.category() : existing.category(),
                command.name() != null ? command.name() : existing.name(),
                command.description() != null ? command.description() : existing.description(),
                command.amount() != null ? command.amount() : existing.amount(),
                command.currencyCode() != null ? command.currencyCode() : existing.currencyCode(),
                command.costDate() != null ? command.costDate() : existing.costDate(),
                existing.status(),
                existing.version(),
                existing.createdAt(),
                existing.updatedAt()
        );

        CustomCost saved = customCosts.save(updated);
        recalculate.execute(command.scenarioId(), command.projectId());

        activityLogger.log(FinanceEntityTypes.CUSTOM_COST, saved.id(),
                FinanceActivityActions.UPDATE_CUSTOM_COST,
                "Custom cost updated: " + saved.name());

        return CustomCostResponse.from(saved);
    }
}
