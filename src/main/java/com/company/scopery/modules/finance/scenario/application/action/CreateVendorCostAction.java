package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.command.CreateVendorCostCommand;
import com.company.scopery.modules.finance.scenario.application.response.VendorCostResponse;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCost;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCostRepository;
import com.company.scopery.modules.finance.shared.activity.FinanceActivityLogger;
import com.company.scopery.modules.finance.shared.constant.FinanceActivityActions;
import com.company.scopery.modules.finance.shared.constant.FinanceEntityTypes;
import com.company.scopery.modules.finance.shared.error.FinanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateVendorCostAction {

    private final FinanceScenarioRepository scenarios;
    private final VendorCostRepository vendorCosts;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public CreateVendorCostAction(FinanceScenarioRepository scenarios,
                                  VendorCostRepository vendorCosts,
                                  RecalculateFinanceSummaryAction recalculate,
                                  FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.vendorCosts = vendorCosts;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public VendorCostResponse execute(CreateVendorCostCommand command) {
        scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(command.scenarioId()));

        VendorCost cost = VendorCost.create(
                command.scenarioId(),
                command.projectPhaseId(),
                command.vendorName(),
                command.description(),
                command.amount(),
                command.currencyCode());

        VendorCost saved = vendorCosts.save(cost);
        recalculate.execute(command.scenarioId(), command.projectId());

        activityLogger.log(FinanceEntityTypes.VENDOR_COST, saved.id(),
                FinanceActivityActions.CREATE_VENDOR_COST,
                "Vendor cost created: " + saved.vendorName());

        return VendorCostResponse.from(saved);
    }
}
