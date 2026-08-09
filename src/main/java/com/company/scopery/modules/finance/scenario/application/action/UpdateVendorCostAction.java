package com.company.scopery.modules.finance.scenario.application.action;

import com.company.scopery.modules.finance.scenario.application.command.UpdateVendorCostCommand;
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
public class UpdateVendorCostAction {

    private final FinanceScenarioRepository scenarios;
    private final VendorCostRepository vendorCosts;
    private final RecalculateFinanceSummaryAction recalculate;
    private final FinanceActivityLogger activityLogger;

    public UpdateVendorCostAction(FinanceScenarioRepository scenarios,
                                  VendorCostRepository vendorCosts,
                                  RecalculateFinanceSummaryAction recalculate,
                                  FinanceActivityLogger activityLogger) {
        this.scenarios = scenarios;
        this.vendorCosts = vendorCosts;
        this.recalculate = recalculate;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public VendorCostResponse execute(UpdateVendorCostCommand command) {
        scenarios.findByIdAndProjectId(command.scenarioId(), command.projectId())
                .orElseThrow(() -> FinanceExceptions.scenarioNotFound(command.scenarioId()));

        VendorCost existing = vendorCosts.findById(command.costId())
                .orElseThrow(() -> FinanceExceptions.costNotFound(command.costId()));

        VendorCost updated = new VendorCost(
                existing.id(),
                existing.financeScenarioId(),
                existing.projectPhaseId(),
                command.vendorName() != null ? command.vendorName() : existing.vendorName(),
                command.description() != null ? command.description() : existing.description(),
                command.amount() != null ? command.amount() : existing.amount(),
                command.currencyCode() != null ? command.currencyCode() : existing.currencyCode(),
                existing.status(),
                existing.version(),
                existing.createdAt(),
                existing.updatedAt()
        );

        VendorCost saved = vendorCosts.save(updated);
        recalculate.execute(command.scenarioId(), command.projectId());

        activityLogger.log(FinanceEntityTypes.VENDOR_COST, saved.id(),
                FinanceActivityActions.UPDATE_VENDOR_COST,
                "Vendor cost updated: " + saved.vendorName());

        return VendorCostResponse.from(saved);
    }
}
